package com.jlab.demo

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkContext._

/**
 * Created by ngvtien on 11/18/2014.
 */
object SimpleJob {
  def main(args: Array[String]) {

    println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
    val logFile = System.getenv("HOME") + "/data/prisonbreakfirst/Prison.Break.S01E01.720p.BluRay.x264-HALCYON.srt"

    val conf = new SparkConf()
      .setMaster("spark://scorpiovn:7077")
      .setAppName("Simple Application")
      .setSparkHome(System.getenv("SPARK_HOME"))
      .set("spark.executor.memory", "512m")
//      .setJars(Array("/home/scorpiovn/git/maven-example/core/target/core-1.0.0.jar"))

    val sc = new SparkContext(conf)

    /*val sc = new SparkContext("spark://scorpiovn:7077",
      "Simple Job",
      System.getenv("SPARK_HOME"),
      Seq("/home/scorpiovn/git/maven-example/core/target/core-1.0.0.jar"))
*/
    val logData = sc.textFile(logFile)

    val numsa = logData.filter(line => line.contains("a")).count

    val numsb = logData.filter(line => line.contains("b")).count

    println("total a : %s, total b : %s".format(numsa, numsb))
  }
}
