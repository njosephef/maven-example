package com.jlab.demo

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._ // import the implicit conversions from SparkContext

/**
 * Created by scorpiovn on 11/25/14.
 */
class Analysis(context: SparkContext, file: String) extends Serializable {

  def this(context: SparkContext) {
    this(context, "");
  }

  def process(out: String): Unit = {

    val data = context.textFile(file)

    val regex = """[0-9,.:;',.\/<>?\-\"]""".r

    data.flatMap(line => line.split("[\\s]"))
      .map(w => regex.replaceAllIn(w.trim.toLowerCase, ""))
      .filter(w => !w.isEmpty)
      .map(w => (w,1))
      .reduceByKey(_+_)
      .map(item => item.swap)
      .groupBy(_._1)
      .sortBy(_._1, false)
      .saveAsTextFile(out);
  }

  def process(): Unit = {
    val data = context.textFile (file);
    val numsa = data.filter (line => line.contains ("a")).count
    val numsb = data.filter (line => line.contains ("b")).count
    println ("total a : %s, total b : %s".format (numsa, numsb))
  }
}
