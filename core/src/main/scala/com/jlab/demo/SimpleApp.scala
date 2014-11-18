package com.jlab.demo

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object SimpleApp {
  def main(args: Array[String]) {
    val logFile = "/home/scorpiovn/apps/README.md" // Should be some file on your system
    val conf = new SparkConf()
                .setMaster("local[2]")
                .setAppName("Simple Application")
                .setSparkHome("/home/scorpiovn/apps/spark-1.1.0-bin-hadoop2.4")
                //.setJars(Array("target/core-1.0.0.jar"))
    val sc = new SparkContext(conf)
    val logData = sc.textFile(logFile, 2).cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()
    println("Lines with a: %s, Lines with b: %s".format(numAs, numBs))
  }
}