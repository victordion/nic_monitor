package info.jcui.nicmonitor

import com.twitter.server.TwitterServer
import java.util.concurrent._
import sys.process._
import com.twitter.finagle.http.Method
import scala.info.jcui.nicmonitor.MySQLClient
import scala.util.Random
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http
import com.twitter.util.{Time, Await, Future}
import org.json4s.native.Json
import org.json4s.DefaultFormats

object Server extends TwitterServer {

  val username = flag[String]("username", "root", "username of MySQL DB")
  val password = flag[String]("password", "", "passowrd of MySQL DB")

  def main(): Unit = {
    try {
      val random = new Random
      val dataStoreClient = new MySQLClient("localhost", username(), password())
      val ex = new ScheduledThreadPoolExecutor(1)

      val service = new Service[http.Request, http.Response] {
        def apply(req: http.Request): Future[http.Response] = {
          try {
            val path = req.path

            Console.println(s"path = $path")
            Console.println(s"method = ${req.method.toString}")

            if (req.method.equals(Method.Get) && (path == "/tx" || path == "/rx")) {

              val endTimeStamp = req.getLongParam("endTimeStamp")
              val spanInMinutes = req.getLongParam("spanInMinutes")

              Console.println(s"endTimeStamp = $endTimeStamp")
              Console.println(s"spanInMinutes = $spanInMinutes")

              val byteType = if(path == "/tx") "tx" else "rx"
              val tsToSizeMap = dataStoreClient.read(spanInMinutes, endTimeStamp, byteType)

              Console.println(s"Returned result: $tsToSizeMap")
              val response = http.Response(req.version, http.Status.Ok)
              response.headerMap.add("Access-Control-Allow-Origin", "*")

              val jsonString = Json(DefaultFormats).write(tsToSizeMap)

              response.setContentString(jsonString)

              Future(response)

            } else {
              val response = http.Response(req.version, http.Status.Ok)
              response.setContentString("You didn't provide extra info")
              Future.value(response)
            }
          } catch { case e: Throwable =>
            Console.println(s"Failed to process HTTP request $req")
            e.printStackTrace()
            val response = http.Response(req.version, http.Status.Ok)
            response.setContentString(s"Failed to process HTTP request $req")
            Future.value(response)

          }
        }

      }

      val task = new Runnable {
        override def run(): Unit = {
          try {
            val ts: Long = System.currentTimeMillis() / 1000

            val rx = ("cat /sys/class/net/eth0/statistics/rx_bytes" lineStream_!).head.toLong
            val tx = ("cat /sys/class/net/eth0/statistics/tx_bytes" lineStream_!).head.toLong

            dataStoreClient.write(ts, tx, rx)

          } catch {
            case e: Throwable =>
              e.printStackTrace()
          }
        }
      }

      val f = ex.scheduleAtFixedRate(task, 1, 1, TimeUnit.SECONDS)

      val httpServer = Http.serve(":9991", service)
      Await.ready(httpServer)

      while (true) {
        Thread.sleep(10000)
      }
    } catch { case e: Throwable =>
        e.printStackTrace()
        System.exit(1)
    }
  }

}