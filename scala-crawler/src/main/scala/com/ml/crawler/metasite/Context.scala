package com.ml.crawler.metasite

import collection.mutable.{HashSet, ArrayBuffer}
import com.ml.crawler.metasite.Facts.Fact

class Context extends Traversable[Fact] {
	private val collection = new HashSet[Fact]()
	private val relation = new HashSet[Relation]()

	def add(f: Fact): Unit = collection.add(f)

	def addRelation(f1: Fact, f2: Fact): Unit = {
		add(f1);
		add(f2);
		relation.add(Relation(f1, f2))
	}

	def bind(f1: Fact) = new {
		def to(f2: Fact) = addRelation(f1, f2)
	}

	def remove(f: Fact) {
		collection.remove(f)
		//TODO: remove relations
	}

	def foreach[U](f: Fact => U) {
		collection.foreach(f)
	}

	def findAll[T]()(implicit m: Manifest[T]): Traversable[T] = {
		collection.filter(f => m.erasure.isAssignableFrom(f.getClass)).map(_.asInstanceOf[T])
	}

	def findAll[T](pred: (T => Boolean))(implicit m: Manifest[T]): Traversable[T] = findAll[T]() filter pred

	def findRelated[T](fact: Fact) = relation.map(_ match {
		case Relation(f1, f2) if f2 == fact => f1
		case Relation(f1, f2) if f1 == fact => f2
		case _ => null
	}).filter(_ != null)
}


object Context {

	import scala.collection.mutable.{Builder, MapBuilder}
	import scala.collection.generic.CanBuildFrom

	def empty = new Context()

//	implicit def canBuildFrom[A]: CanBuildFrom[Context[_], A, Context[A]] = null

//	def newBuilder[A]: Builder[A, Context[A]] = null; // new ArrayBuffer[A]

	//
	//	def newBuilder[T]: Builder[Fact, PrefixMap[T]] =
	//		new MapBuilder[String, T, PrefixMap[T]](empty)

	//	implicit def canBuildFrom[T]
	//	: CanBuildFrom[PrefixMap[_], (String, T), PrefixMap[T]] =
	//		new CanBuildFrom[PrefixMap[_], (String, T), PrefixMap[T]] {
	//			def apply(from: PrefixMap[_]) = newBuilder[T]
	//
	//			def apply() = newBuilder[T]
	//		}
}

case class Relation(fact1: Fact, fact2: Fact) {

}