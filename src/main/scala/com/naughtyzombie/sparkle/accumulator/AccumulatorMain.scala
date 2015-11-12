package com.naughtyzombie.sparkle.accumulator

import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.slf4j.LoggerFactory

/**
  * Created by pram on 12/11/2015.
  */
object AccumulatorMain {

  def logger = LoggerFactory.getLogger("AccumulatorMain")

  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      logger.error(s"Usage: AccumulatorMain sparkmaster inputfile")
      sys.exit(1)
    }

    val master = args(0)
    val inputFile = args(1)

    val sc = new SparkContext(master, "AccumulatorMain", System.getenv("SPARK_HOME"))

    val file: RDD[String] = sc.textFile(inputFile)

    val sillyLineCount  = sc.accumulator(0)

    file.foreach(line => {
      sillyLineCount += 1
    })

    println(s"Silly Line Count = $sillyLineCount")

  }
}
