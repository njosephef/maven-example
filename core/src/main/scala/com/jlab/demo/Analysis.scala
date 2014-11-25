package com.jlab.demo

import org.apache.spark.SparkContext

/**
 * Created by scorpiovn on 11/25/14.
 */
class Analysis(context: SparkContext, file: String) {
  def process(out: String): Unit = {
    val data = context.textFile (file);
    data.saveAsTextFile(out);
  }

  def process(): Unit = {
    val data = context.textFile (file);
    val numsa = data.filter (line => line.contains ("a")).count
    val numsb = data.filter (line => line.contains ("b")).count
    println ("total a : %s, total b : %s".format (numsa, numsb))
  }
}
