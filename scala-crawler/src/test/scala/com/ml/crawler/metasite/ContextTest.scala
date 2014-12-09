package com.ml.crawler.metasite

import org.junit._
import org.junit.Assert._
import com.ml.crawler.metasite.Facts.Fact

class ContextTest {
	@Test
	def test() {
		val c = new Context()
		case class MyFact(data: String) extends Fact;
		val f1 = MyFact("abc1")
		val f2 = MyFact("abc2")
		val f3 = MyFact("abc3")
		c.add(f1)
		c.add(f2)
		c.add(f3)

		c bind f1 to f2

//		assertEquals(List(Relation(f1, f2)), c.findAll[Relation]())
		assertEquals(3, c.findAll[MyFact]().size)
//		assertEquals(4, c.findAll[Fact]().size)
		assertEquals(Set(f1), c.findRelated[Fact](f2))
	}
}