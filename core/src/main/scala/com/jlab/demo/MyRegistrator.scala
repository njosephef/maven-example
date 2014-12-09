package com.jlab.demo

import com.twitter.chill.Kryo
import com.twitter.chill.java.RegexSerializer
import org.apache.spark.serializer.KryoRegistrator

import scala.util.matching.Regex

/**
 * Created by scorpiovn on 12/2/14.
 */
class MyRegistrator extends KryoRegistrator {
  override def registerClasses(kryo: Kryo) {
    kryo.register(classOf[Regex], new RegexSerializer())
  }
}
