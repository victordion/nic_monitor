package scala.info.jcui.nicmonitor


import java.sql.ResultSet

import com.twitter.querulous.evaluator.QueryEvaluator
import com.twitter.querulous.query.QueryClass

/**
  * Created by jcui on 3/1/17.
  */
class MySQLClient(host: String, username: String, password: String) {

  val createTableSql = """ CREATE TABLE IF NOT EXISTS nic_status
      (ts BIGINT NOT NULL,
       tx BIGINT NOT NULL,
       rx BIGINT NOT NULL,
       PRIMARY KEY (ts)
      );
     """

  //val createDbSql = """ CREATE DATABASE IF NOT EXISTS nic;"""
  val queryEvaluator = QueryEvaluator(host, "nic", username, password)
  val readQueryEvaluator = QueryEvaluator(host, "nic", username, password)

  //queryEvaluator.execute(createDbSql)
  //queryEvaluator.execute("use nic;")
  queryEvaluator.execute(createTableSql)

  def write(ts: Long, tx: Long, rx: Long): Unit = {
    queryEvaluator.execute("INSERT INTO nic_status VALUES (?, ?, ?)", ts, tx, rx)
  }

  def read(timeSpanInMinutes: Long, endTimeStamp: Long, byteType: String): Map[Long, Long] = {
    val startTimeStamp = endTimeStamp - timeSpanInMinutes * 60
    val selectQuery = "SELECT ts, %s FROM nic.nic_status WHERE ts > %d AND ts < %d;".format(byteType, startTimeStamp, endTimeStamp)

    Console.println(s"Queury: $selectQuery")

    val tsSizeTuples = readQueryEvaluator.select(selectQuery) {
      r: ResultSet =>
        (r.getLong(1), r.getLong(2))
    }

    tsSizeTuples.toMap
  }

}
