package com.ml.crawler.pattern

import com.ml.crawler.Beta

trait BaseGExprLike {
  protected type MMatch
  protected type MMatcher <: Matcher
  protected val companionObject: BaseGExprLikeObject
  val current: Matcher;

  def || (m2: Matcher) = new Or (flatten (classOf[Or], current, m2))

  def :: (m2: Matcher) = new Seq (flatten (classOf[Seq], m2, current))

  def times (min: Int, max: Int) = new Star (current, min, max)

  def times (count: Int): Star = times (count, count)

  def < (m2: Matcher): Matcher

  def > (m2: Matcher) = companionObject.matcher2gexp (m2) < current


  def star = times (0, Int.MaxValue)

  def plus = times (1, Int.MaxValue)

  def opt = times (0, 1)

  def * = star

  def + = plus

  def ? = opt

  def cut = new CutMatcher (current)

  def % (name: String): MMatcher

  //	def %(symbol: Symbol): MMatcher = %(symbol.name)

  def ? (predicate: (MMatch => Boolean)): Matcher

  protected def flatten (cls: Class[_], matcher: Matcher*): List[Matcher] = {
    def toList (m: Matcher) = if (cls == m.getClass) m.getSubMatchers.toList else List (m)
    matcher.toList.flatMap (toList (_)).toList
  }

  @Beta
  protected def newMatcher (func: ((Node, GraphContext) => Traversable[Node]), matchers: Matcher*): Matcher = new Matcher () {
    def find (node: Node, context: GraphContext) = new MatchResult {
      val col = func (node, context)

      def nextInner () = null
    }

    def getSubMatchers = matchers
  }

  @Beta
  protected def newMatcher (func: (Node => Traversable[Node]), matchers: Matcher*): Matcher = newMatcher ({
    (n: Node, c: GraphContext) => func (n)
  }, matchers: _*)
}

trait BaseGExprLikeObject {
  type GExprLikeType <: BaseGExprLike

  def tag (str: String*): Matcher

  implicit def matcher2gexp (m: Matcher): GExprLikeType

  implicit def symbol2matcherLike (s: Symbol) = matcher2gexp (tag (s.name))

  implicit def symbol2matcher (s: Symbol) = tag (s.name)

  val any: Matcher = new PredicateMatcher (PredicateUtils.truePredicate ())
  val empty: Matcher = new Star (any, 0, 0);
}