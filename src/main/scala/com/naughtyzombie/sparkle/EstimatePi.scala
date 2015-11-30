package com.naughtyzombie.sparkle

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by pram on 29/11/2015.
  */
object EstimatePi {
  def main(args: Array[String]): Unit = {

    val NUM_SAMPLES = 10000
    val conf = new SparkConf().setAppName("Spark Pi")
    val sc = new SparkContext("local", "Spark Pi", conf)

    val count = sc.parallelize(1 to NUM_SAMPLES).map { i =>
      val x = Math.random()
      val y = Math.random()

      if (x * x + y * y < 1) 1 else 0
    }.reduce(_ + _)

    println(s"The value of pi is roughly ${ 4.0 * count / NUM_SAMPLES}")

//    val x = sc.textFile("hdfs://quickstart.cloudera:9000/user/pram/pi-out")

    sc.stop()


  }

}
