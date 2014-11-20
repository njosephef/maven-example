package com.jlab.demo

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object SimpleApp {
  def main(args: Array[String]) {
    val logFile = System.getenv("HOME") +"/data/prisonbreakfirst/Prison.Break.S01E01.720p.BluRay.x264-HALCYON.srt"

    val conf = new SparkConf()
                .setMaster("local[2]")
                .setAppName("Simple Application")
                .setSparkHome(System.getenv("SPARK_HOME"))
                //.setJars(Array("target/core-1.0.0.jar"))
    val sc = new SparkContext(conf)
    val logData = sc.textFile(logFile, 2).cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()
    println("Lines with a: %s, Lines with b: %s".format(numAs, numBs))
  }
}