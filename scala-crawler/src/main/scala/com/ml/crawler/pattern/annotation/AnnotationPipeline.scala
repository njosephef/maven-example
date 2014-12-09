package com.ml.crawler.pattern.annotation

import java.io.InputStream
import java.net.URL
import java.nio.charset.Charset

import com.ml.crawler.pattern._

object AnnotationPipeline extends BaseGExprLikeObject {
  type Ann = TextAnnotator


  type GExprLikeType = MatcherLike

  implicit def matcher2gexp (m: Matcher) = new MatcherLike (m)

  implicit def pattern (matcher: Matcher) = Annotators.pattern (matcher)

  implicit def wrapper2like (matcher: GraphMatchWrapper) = new GraphMatchWrapperLike (matcher)

  implicit def textAnnotator2rich (a: Ann) = new TextAnnotatorLike (a)

  implicit def an2rich (an: Annotation) = new AnnotationLike (an)

  def parseURL (url: URL) = {
    val (data, cs) = inputStream2String (url.openStream ())
    parseContent (data, url.toString, cs.name ())
  }

  def parseContent (html: String, url: String = "http://no_url", enc: String = "utf8"): AnnotatedText = HtmlAnnotatedTextImporter.createAnnotatedText (
    url, Jsoup.parse (html, url).html ().getBytes (enc), enc)

  private def inputStream2String (is: InputStream, coding: String = null): (String, Charset) = {
    import com.ibm.icu.text.CharsetDetector
    val detector = new CharsetDetector
    detector.enableInputFilter (true)
    detector.setDeclaredEncoding (coding)
    detector.setText (IOUtils.toByteArray (is))
    val charsetMatch = detector.detect ()
    (charsetMatch.getString, import com.ml.crawler.pattern.Binder._ (charsetMatch.getName))
  }

  def execute (an: Ann, text: AnnotatedText) {
    text.getAnnotations.addAll (an.annotate (text))
  }

  def regexp (reg: String, anType: String, group: Int = 0) = Annotators.regexp (reg, group, anType)

  //pattern
  def tag (str: String*) = PatternGraphAnnotator.`match` (str: _*)

  class TextAnnotatorLike (current: Ann) {
    def :: (a2: Ann) = Annotators.pipeline (a2, current)

    def ::: (a2: Ann) = Annotators.all (a2, current)

    def /: (a2: Ann) = Annotators.temp (a2, current)

    def < (a2: Ann) = Annotators.within (current, a2)
  }

  class MatcherLike (val current: Matcher) extends BaseGExprLike {
    type M = Matcher
    type MMatch = GraphMatchWrapper
    type MMatcher = PatternGraphAnnotator.MarkMatcher
    protected val companionObject = AnnotationPipeline

    def % (name: String) = PatternGraphAnnotator.mark (name, current)

    def %% (name: String) = PatternGraphAnnotator.mark (name, current).setGenerateAnnot (false)

    def % (func: (GraphMatchWrapper => Unit)) = PatternGraphAnnotator.mark (null, current).setAction (new MatchAction {
      def execute (text: AnnotatedText, result: AnnotationSet, mm: GraphMatchWrapper, createdAnnotation: Annotation) {
        func (mm)
      }
    })

    def < (m2: Matcher) = PatternGraphAnnotator.insideFind (current, m2)

    def << (m2: Matcher) = PatternGraphAnnotator.boundedMatchWithin (current, m2)

    def <* (m2: Matcher) = PatternGraphAnnotator.insideFindAll (current, m2)

    def withContext = PatternGraphAnnotator.context (current)

    def weight (evaluator: (MMatch => Double)) = PatternGraphAnnotator.weighted (current, new Transformer[GraphMatchWrapper, java.lang.Double] {
      def transform (p: GraphMatchWrapper) = evaluator (p)
    })

    def ? (predicate: (MMatch => Boolean)) = PatternGraphAnnotator.`match` (current, new Predicate[GraphMatchWrapper]() {
      def evaluate (   })


  }

}

class GraphMatchWrapperLike (val m: GraphMatchWrapper) {
  def bind[T <: AnyRef] (value: T, spec: (T => Unit) = null): T = {
    import Binder._
    for (name <- m.getAllGroupNames) {
      value.setV (name, m.get (name).getText)
    }
    if (spec != null) spec (value)
    value
  }

  def first: Annotation = m.getAnnotationList.get (0)
}

class AnnotationLike (an: Annotation) {
  def attr (s: Symbol, value: String): String = {
    val prev = attr (s);
    an.getFeatureMap.putRawItem (s.name, value);
    prev
  }

  def attr (s: Symbol): String = an.getFeatureMap.getRawItem (s.name)
}

class AnnotatedTextLike (anText: AnnotatedText) {

}

