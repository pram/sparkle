package com.naughtyzombie.sparkle.recipesearch

import com.naughtyzombie.sparkle.recipesearch.model.Recipe
import org.apache.spark._
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.DeserializationFeature
import org.slf4j.LoggerFactory

/**
  * Created by pram on 05/11/2015.
  */
object RecipeMain {
  def logger = LoggerFactory.getLogger("RecipeMain")
  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      logger.error(s"Usage: RecipeMain sparkmaster inputfile outputfile")
      exit(1)
    }

    val master = args(0)
    val inputFile = args(1)
    val outputFile = args(2)

    val sc = new SparkContext(master, "RecipeMain",System.getenv("SPARK_HOME"))

    val input = sc.textFile(inputFile)

    val result = input.mapPartitions(records => {
      val mapper = new ObjectMapper with ScalaObjectMapper
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      mapper.registerModule(DefaultScalaModule)

      records.flatMap(record => {
        try {
          Some(mapper.readValue(record, classOf[Recipe]))
        } catch {
          case e: Exception => None
        }
      })
    }, true)
  }
}
