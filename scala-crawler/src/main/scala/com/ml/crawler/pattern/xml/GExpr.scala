package com.ml.crawler.pattern.xml

import scala.collection.JavaConversions._
import org.jsoup.nodes.{Node => JSNode, _}
import org.apache.commons.collections15.Predicate
import com.ml.crawler.pattern.xml.impl._
import com.ml.graph.matcher.GraphRegExp._
import com.ml.crawler.pattern.BaseGExprLike


class GExpr (val current: Matcher) extends BaseGExprLike {
  //	val current = current
  protected type MMatch = MatchAdapter
  protected type MMatcher = Matcher
  protected val companionObject = GEximport com.ml.crawler.pattern.xml.GExpr._
import com.ml.crawler.pattern.xml.impl.GExprImpl._GExpr._

  def -:: (m2: Matcher) = splitMatcher (m2, current, (e =>
    if (e.parent () != null && e.previousElementSibling () != null) Iterable (e.previousElementSibling ())
    else Iterable.empty)
  )

  def ::: (m2: Matcher) = new Seq (List (m2, new GExpr (any).star, current))

  //split
  def <:: (m2: Matcher) = splitMatcher (m2, current, (e => Iterable (e)))

  //tree
  def < (m2: Matcher) = splitMatcher (current, m2, (e => e.children ()))

  def << (m2: Matcher) = splitMatcher (current, m2, (e => getAllChildren (e).drop (1)))

  override def > (m2: Matcher) = splitMatcher (current, m2, (e => Iterable (e.parent ())))

  def >> (m2: Matcher) = splitMatcher (current, m2, (e => e.parents ()))

  def jquery (query: String)(m2: Matcher) = splitMatcher (current, m2, (e => e.select (query)))

  def % (name: String) = transformedMatcher (current)(m => new MarkMatch (name, m))

  def ? (predicate: (MatchAdapter => Boolean)) = transformedMatcher (current)(m => if (predicate (new MatchAdapter (m))) m else null)
}

object GExpr extends BaseGExprLikeObject {
  type GExprLikeType = GExpr

  implicit def matcher2gexp (m: Matcher) = new GExpr (m)

  implicit def tag import com.ml.crawler.pattern.xml.impl.GExprImpl._predicate (el => strs.exists (_ == el.tagName ()))

  import GExprImpl._

  def search (matcher: Matcher, root: Element)(exec: (MatchAdapter => Unit)) = {
    getAllChildren (root).foreach {
      ch =>
        val mr: MatchResult = matcher.find (createStartNode (ch), new JsoupContext)
        if (mr.next () != null) {
          exec (new MatchAdapter (mr.current ()))
        }
    }
  }


  def predicate (pred: (Element => Boolean)) = new PredicateMatcher (new Predicate[Edge] {
    def evaluate (edge: Edge) = pred (fromEdge (edge).end.element)
  })
};
