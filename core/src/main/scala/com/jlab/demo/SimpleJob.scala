package com.jlab.demo

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

/**
 * Created by ngvtien on 11/18/2014.
 */
object SimpleJob {
  def main(args: Array[String]) {
    val logFile = "/home/training/data/prisonbreakfirst/Prison.Break.S01E01.720p.BluRay.x264-HALCYON.srt"
    val sc = new SparkContext("spark://localhost.localdomain:7077", "Simple Job",
      System.getenv("SPARK_HOME"), Seq("<JAR File Address>"))
    val logData = sc.textFile(logFile)
    val numsa = logData.filter(line => line.contains("a")).count
    val numsb = logData.filter(line => line.contains("b")).count
    println("total a : %s, total b : %s".format(numsa, numsb))
  }
}
