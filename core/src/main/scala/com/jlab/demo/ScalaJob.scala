package com.jlab.demo

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

/**
 * Created by ngvtien on 11/18/2014.
 */
object ScalaJob {
  def main (args: Array[String]) {
    
    val logFile = System.getenv ("HOME") + "/data/prisonbreakfirst/Prison.Break.S01E01.720p.BluRay.x264-HALCYON.srt"
    //val sc = new SparkContext ("spark://localhost.localdomain:7077"
				//, "Simple Job"
				//, System.getenv ("SPARK_HOME")
				//, Seq (System.getenv ("HOME") + "/git/maven-example/core/target/core-1.0.0.jar"))
				//
				//
    val conf = new SparkConf()
                //.setMaster("spark://localhost.localdomain:7077")
                .setAppName("SimpleJob")
                .setSparkHome(System.getenv("SPARK_HOME"))
                //.setJars(Array(System.getenv ("HOME") + "/git/maven-example/core/target/core-1.0.0.jar"))

    val sc = new SparkContext(conf)

    val analysis = new Analysis(sc, logFile);
    analysis.process();

    /*val logData = sc.textFile (logFile)
    val numsa = logData.filter (line => line.contains ("a")).count
    val numsb = logData.filter (line => line.contains ("b")).count
    println ("total a : %s, total b : %s".format (numsa, numsb))*/
  }
}
