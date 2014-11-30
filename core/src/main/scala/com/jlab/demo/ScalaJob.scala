package com.jlab.demo

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

import scala.reflect.io.Path
import scala.util.Try


/**
 * Created by ngvtien on 11/18/2014.
 */
object ScalaJob {
  def main (args: Array[String]) {

    val logFile = System.getenv ("HOME") + "/data/prisonbreakfirst/*.srt"
    val conf = new SparkConf()
      .setAppName("SimpleJob")
      .setSparkHome(System.getenv("SPARK_HOME"))
      //.setJars(Array(System.getenv ("HOME") + "/git/maven-example/core/target/core-1.0.0.jar"))

    val sc = new SparkContext(conf)

    val analysis = new Analysis(sc, logFile);

    val out = "/home/scorpiovn/sparkout/out1"
    IOManager.delete(out)

    analysis.process(out);
    analysis.process()
  }
}
