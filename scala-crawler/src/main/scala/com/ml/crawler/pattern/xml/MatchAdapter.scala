package com.ml.crawler.pattern.xml

import scala.collection.JavaConversions._
import org.jsoup.nodes.{Node => JSNode, _}
import com.ml.crawler.pattern.xml.impl._
import com.ml.graph.matcher.GraphRegExp._
class MatchAdapter(val m: Match) {

	import collection.mutable._
	import GExprImpl._

	lazy val elements: List[Element] = m.getList.map(_.end.element).toList
	lazy val allElements: List[Element] = {
		def getAll(m: Match): List[Edge] = if (m.getSubMatches.isEmpty) m.getList.toList else m.getSubMatches.toList.flatMap(getAll(_))
		getAll(m).map(_.end.element).toList
	}

	private lazy val map: Map[String, Set[MatchAdapter]] = {

		def getAll(m: Match): List[Match] = m :: m.getSubMatches.flatMap(getAll(_)).toList
		val map = new HashMap[String, Set[MatchAdapter]] with MultiMap[String, MatchAdapter]
		getAll(m).foreach {
			_ match {
				case m: MarkMatch => map.addBinding(m.name, new MatchAdapter(m))
				case _ =>;
			}
		}
		map
	}

	def head = elements.head

	def get(v1: String): MatchAdapter = map.get(v1) match {
		case Some(col) => col.head
		case _ => null
	}

	def getAll(v1: String): Set[MatchAdapter] = map.getOrElse(v1, Set())

	def attr(name: String) = head.attr(name)

	def text = elements.map(_.text()).mkString(" ")

	def html = elements.map(_.html()).mkString(" ")

	def tag = head.tagName()

	val name = m match {
		case m: MarkMatch => m.name
		case _ => null
	}
}






;
