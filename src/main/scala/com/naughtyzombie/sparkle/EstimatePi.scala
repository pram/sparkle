package com.naughtyzombie.sparkle

import org.apache.spark.SparkContext

/**
  * Created by pram on 29/11/2015.
  */
object EstimatePi {
  def main(args: Array[String]): Unit = {

    val sc = new SparkContext()

    val count = sc.parallelize(1 to 10).map { i =>
      val x = Math.random()
      val y = Math.random()

      if (x * x + y * y < 1) 1 else 0
    }.saveAsTextFile("hdfs://quickstart.cloudera:9000/user/pram/pi-out")

//    val x = sc.textFile("hdfs://quickstart.cloudera:9000/user/pram/pi-out")

    sc.stop()


  }

}
