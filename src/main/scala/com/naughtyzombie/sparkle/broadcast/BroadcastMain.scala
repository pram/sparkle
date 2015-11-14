package com.naughtyzombie.sparkle.broadcast

import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.slf4j.LoggerFactory

/**
  * Created by pram on 14/11/2015.
  */
object BroadcastMain {

  def logger = LoggerFactory.getLogger("BroadcastMain")

  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      logger.error(s"Usage: BroadcastMain sparkmaster")
      sys.exit(1)
    }

    val master = args(0)

    val sc = new SparkContext(master, "BroadcastMain", System.getenv("SPARK_HOME"))

    val broadcast: Broadcast[String] = sc.broadcast("Test Value")

    println(broadcast.value)
  }

}
