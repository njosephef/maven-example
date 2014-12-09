package com.ml.crawler.metasite

import com.ml.crawler.metasite.Facts.Fact

class Extractor {
  def in (ex: Extractor): Extractor = this

  def after (ex: Extractor): Extractor = this

  def before (ex: Extractor): Extractor = this

  def near (ex: Extractor): Extractor = this

  def as (s: Symbol): Extractor = this

  def && (ex: Extractor): Extractor = this and ex

  def and (ex: Extractor): Extractor = this

  private def toMatcher: Matcher = null

  private def toPrecondition: TextAnnotator = null
}

object Extractor {
  private val empty = new Extractor ()

  implicit def createFromFact[F <: Fact] (f: F): Extractor = createFromFact (f.getClass.asInstanceOf[Class[F]])

  implicit def createFromFact[F <: Fact] (clazz: Class[F]): Extractor = {
    empty
  }

  def Paragraph (ex: Extractor = empty): Extractor = empty

  def Column (ex: Extractor = empty): Extractor = empty

  def Column (number: Int): Extractor = empty

  def Table (columns: Int): Extractor = empty

  def Block (ex: Extractor = empty): Extractor = empty

  def MainBlock: Extractor = empty

  def extract (ex: Extractor*) {} //(f:(Context,Map[Symbol, Fact])=>Unit)
}