package com.ml.crawler.pattern.xml.impl

import scala.annotation.tailrec
import scala.collection.JavaConversions._

object GExprImpl {
  implicit def fromEdge (value: Edge): GEEdge = value.asInstanceOf[GEEdge]

  def getAllChildren (e: Element): Stream[Element] = {
    val ch = e.children ().toStream
    if (ch.nonEmpty) {
      Stream (e) #::: ch.flatMap (child => getAllChildren (child))
    } else {
      Stream (e)
    }
  }

  //
  def splitMatcher (m: Matcher, m2: Matcher, f: (Element => Iterable[Element])): Matcher = {
    val matcher = new Matcher () {
      def find (startNode: Node, context: GraphContext) = new MatchResult () {
        private val elIt = f (fromNode (startNode).element).toIterator

        def nextInner () = {
          if (elIt.hasNext) {
            val endNode = createStartNode (elIt.next ())
            new LeafMatch (context, startNode, endNode, List (new GEEdge (startNode, endNode)))
          } else null;
        }
      }

      def getSubMatchers = List ()
    }

    val seqMatcher = new Seq (List (m, matcher, m2))

    transformedMatcher (seqMatcher)(m => new JoinMatch (m.getSubMatches.get (0), m.getSubMatches.get (2)))
  }

  implicit def fromNode (value: Node): GENode = value.asInstanceOf[GENode]

  def createStartNode (e: Element) = new StartGENode (e)

  def transformedMatcher (m: Matcher)(transform: (Match => Match)): Matcher = new Matcher () {
    def find (node: Node, context: GraphContext) = new MatchResult () {
      val topMR = m.find (node, context)

      @tailrec
      def nextInner (): Match = {
        if (topMR.next () != null) {
          val res = transform (topMR.current ());
          if (res != null) res
          elsimport com.ml.crawler.pattern.xml.impl.GExprImpl._ ()
        }
        else null
      }
    }

    def getSubMatchers = m.getSubMatchers
  }
}


class JsoupContext extends GraphContext {

  import GExprImpl._

  def getEdges (node: Node): java.lang.Iterable[Edge] = asJavaIterable (
    if (node.sibling != null) List (new GEEdge (node, node.sibling)) else List.empty
  )


  def getEndNode (edge: Edge): Node = edge.end

  def put[T] (key: String, value: T): T = null.asInstanceOf[T]

  def get[T] (key: String): T = null.asInstanceOf[T]
}


class GENode (el: Element) extends Node {
  //
  lazy val sibling = if (el.parent != null) {
    val sibling = el.nextElementSibling ();
    if (sibling != null) new GENode (sibling) else null
  } else null

  def element = el

  def children = el.children ().toList

  def parent = if (el.parent () != null) new GENode (el.parent ()) else null
}

class StartGENode (el: Element) extends GENode (el) {
  override lazy val sibling = new GENode (el)
}

class GEEdge (start: GENode, val end: GENode) extends Edge;

