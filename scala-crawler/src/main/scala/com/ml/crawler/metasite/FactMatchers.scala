package com.ml.crawler.metasite

import com.ee.core.common.TextAnnotator
import com.ml.graph.matcher.GraphRegExp._
import com.ml.crawler.metasite.Facts.Fact
import com.ee.core.CorpusTransformer
import collection.mutable.{LinkedHashSet, ArrayBuffer}
import com.ee.core.matcher.GraphMatchWrapper
import scala.collection.JavaConversions._

object FactMatchers {

	import com.ml.crawler.pattern.annotation.AnnotationPipeline._

	trait ExecuterContext {
		//val corpusTransformer = new CorpusTransformer();
		val annots = new LinkedHashSet[TextAnnotator]

		def push(an: TextAnnotator) {
			if (an == null || annots.contains(an)) return;
			an match {
				case pr: Precondition => {
					push(pr.precondition)
					annots += an
					push(pr.postcondition)

				}
				case _ => annots += an
			}
		}

//		def toAnnotator:TextAnnotator = annots.foldLeft(empty)(_ :: _)
	}

	trait Precondition {
		//		this: Matcher =>
		def precondition: TextAnnotator;
		def postcondition:TextAnnotator

		val uid: String = null

		//		abstract override def equals(obj: AnyRef) =
		//			if (uid != null) {
		//				obj match {
		//					case p: Precondition if p.uid != null => p.uid == uid
		//					case _ => super.equals(obj)
		//				}
		//			} else {
		//				super.equals(obj)
		//			}
	}

	def phoneMatcher: Matcher = throw new UnsupportedOperationException

	def emailMatcher: Matcher = throw new UnsupportedOperationException

	def addressMatcher: Matcher = throw new UnsupportedOperationException

	def companyMatcher: Matcher = throw new UnsupportedOperationException

	def personMatcher: Matcher = throw new UnsupportedOperationException

	def dateMatcher: Matcher = throw new UnsupportedOperationException

	def locationMatcher: Matcher = throw new UnsupportedOperationException

	//	def cityMatcher():Matcher;
	//	def cityMatcher():Matcher;

	def Table(columns: Int = -1): Matcher = {
		((('tr << ('td % "column").plus.cut) % "row").plus ? {
			ctx =>
				ctx.getList("row").map (_.getList("column").size).toSet.size == 1
		}) % "table"
	}

}