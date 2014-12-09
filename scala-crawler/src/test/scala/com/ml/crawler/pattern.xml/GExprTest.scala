package com.ml.crawler.pattern.xml

import org.junit._
import com.ml.crawler.pattern.xml.GExpr._
import com.ml.graph.matcher.GraphRegExp.Matcher
import org.jsoup.Jsoup
import org.junit.Assert._
import scala.collection.JavaConversions._

@Test
class GExprTest {

	val text1 = "<html><n1>aaa</n1><n2>aaa</n2><n3>aaa</n3></html>"

	@Test
	def testSize() {
		def assertSizeAndType[T](m: Matcher, cls: Class[T], size: Int) = {
			assertEquals(m.getClass, cls)
			assertEquals(size, m.getSubMatchers.size)
		}
		import com.ml.graph.matcher.GraphRegExp._;
		assertSizeAndType(tag("n1") :: tag("n2") :: tag("n2") :: tag("n2") :: tag("n2") :: tag("n2"), classOf[Seq], 6)
		assertSizeAndType(tag("n1") || tag("n2") || tag("n2") || tag("n2"), classOf[Or], 4)
		assertSizeAndType(tag("n1") || tag("n2") || new Or(List(tag("n2"), tag("n2"))), classOf[Or], 4)
	}

	@Test
	def testSimple() = {
		assertMatch(text1, tag("n1"), "List(n1)")
		assertMatch(text1, tag("n1") :: tag("n2"), "List(n1, n2)")
		assertMatch(text1, tag("n1") ::: any, "List(n1, n2, n3)")
	}

	@Test
	def testReverseSimple() = {
		assertMatch(text1, tag("n2") -:: any, "List(n2, n1)")
		assertMatch(text1, tag("n2") -:: tag("n1"), "List(n2, n1)")
		assertMatch(text1, tag("n1") -:: any, "")
	}


	@Test
	def testTimesAndAny() = {
		assertMatch(text1, tag("n1") :: any.times(1, 3), "List(n1, n2, n3)")
		assertMatch(text1, tag("n1") :: (any +) , "List(n1, n2, n3)")
		assertMatch(text1, tag("n1") :: any.times(1, 3).relucant(), "List(n1, n2)")
	}

	//contains
	val text2 = "<html><n1>aaa<n11 hm='attr'>a<n111>a</n111></n11><n12>a</n12></n1><n2>aaa</n2><n3>aaa</n3></html>"

	@Test
	def testSeqWithHierarchy() = assertMatch(text2, tag("n1") :: any.times(1, 3), "List(n1, n2, n3)")

	@Test
	def testSimpleContains() = {
		assertMatch(text2, tag("n1") < tag("n11"), "List(n1, n11)")
		assertMatch(text2, tag("n1") < tag("n12"), "List(n1, n12)")
		assertMatch(text2, tag("n1") < tag("n1"), "")
	}

	@Test
	def testDeepContains() = {
		assertMatch(text2, tag("n1") << tag("n11"), "List(n1, n11)")
		assertMatch(text2, tag("n1") << tag("n12"), "List(n1, n12)")
		assertMatch(text2, tag("n1") << tag("n1"), "")
	}

	@Test
	def testContainsWithSeq() = {
		assertMatch(text2, (tag("n1") < tag("n11") :: 'n12) :: tag("n2") :: tag("n3"), "List(n1, n11, n12, n2, n3)")
		assertMatch(text2, ('n1 < ('n11 < 'n111) :: 'n12) :: 'n2 :: 'n3, "List(n1, n11, n111, n12, n2, n3)")
	}

	@Test
	def testContainedSimple() = {
		assertMatch(text2, tag("n11") > any, "List(n11, n1)")
		assertMatch(text2, tag("n11") > tag("n11"), "")
		assertMatch(text2, tag("n11") >> any, "List(n11, n1)")
		assertMatch(text2, tag("n111") >> tag("n1"), "List(n111, n1)")

		assertMatch(text2, (tag("n11") > any) :: tag("n12"), "List(n11, n1, n12)")
	}

	@Test
	def testMarks() = {
		assertMatch(text2, tag("n11") > any % "my_tag", "List(n11, n1)",
			((m: MatchAdapter) => assertNotNull(m.get("my_tag")))
		)
		assertMatch(text2, tag("n11") > any % "my_tag", "List(n11, n1)",
			((m: MatchAdapter) => assertNull(m.get("mytag")))
		)
	}

	@Test
	def testGetAttrs() = {
		assertMatch(text2, tag("n11") % "tag" > any, "List(n11, n1)",
			(m => {
				assertEquals("aa", m.get("tag").text)
				assertEquals("a\n<n111>\n a\n</n111>", m.get("tag").html)
				assertEquals("attr", m.get("tag").attr("hm"))
				assertEquals("", m.get("tag").attr("nonExsisting"))
			})
		)
	}


	@Test
	def testPredicate() = {
		assertMatch(text2, any ? (m => m.tag == "n1"), "List(n1)")
	}

	//	@Test
	//	def testSplit() {
	//		assertMatch(text2, (tag("n1") <:: tag("n2")), "List(n1)")
	//	}


	private def assertMatch(html: String, m: Matcher, result: String, handler: (MatchAdapter => Unit) = (m => true)) {
		val sb = new StringBuilder
		val res = Jsoup.parse(html)
		//		println(res.html())
		println(m.getClass.toString + " dddd " + m.getSubMatchers.size)
		search(m, res) {
			m: MatchAdapter =>
				handler(m)
				println("found")
				sb.append(m.allElements.map(_.tagName).toList);
		}
		assertEquals(result, sb.toString())
	}

	//    @Test
	//    def testKO() = assertTrue(false)

}


