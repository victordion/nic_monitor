package scala.info.jcui.nicmonitor


import java.sql.{ResultSet, DriverManager, Connection}

/**
  * Created by jcui on 3/1/17.
  */
class MySQLClient(host: String, username: String, password: String) {
  Class.forName("com.mysql.jdbc.Driver")
  val driver = "com.mysql.jdbc.Driver"
  val url = "jdbc:mysql://" + host + ":3306/nic"
  val connection: Connection = DriverManager.getConnection(url, username, password)
  val statement = connection.createStatement()

  val createTableSql = """ CREATE TABLE IF NOT EXISTS nic_status
      (ts BIGINT NOT NULL,
       tx BIGINT NOT NULL,
       rx BIGINT NOT NULL,
       PRIMARY KEY (ts)
      );
     """

  statement.executeUpdate(createTableSql)

  def write(ts: Long, tx: Long, rx: Long): Unit = {
    val sql = "INSERT INTO nic_status VALUES (%d, %d, %d)".format(ts, tx, rx)
    statement.executeUpdate(sql)
    Console.println("Writing to DB: " + sql)
  }

  def read(timeSpanInMinutes: Long, endTimeStamp: Long, byteType: String): Map[Long, Long] = {
    val startTimeStamp = endTimeStamp - timeSpanInMinutes * 60
    val selectQuery = "SELECT ts, %s FROM nic.nic_status WHERE ts > %d AND ts < %d;".format(byteType, startTimeStamp, endTimeStamp)

    Console.println(s"Reading to DB: $selectQuery")

    val resultSet: ResultSet = statement.executeQuery(selectQuery)

    val ret = new scala.collection.mutable.HashMap[Long, Long]

    while(resultSet.next()) {
      val ts = resultSet.getLong(1)
      val data = resultSet.getLong(2)
      ret += (ts -> data)
    }

    ret.toMap
  }

}
