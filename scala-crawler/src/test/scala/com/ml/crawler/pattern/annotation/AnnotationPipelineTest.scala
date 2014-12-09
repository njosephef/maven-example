package com.ml.crawler.pattern.annotation

import org.junit._
import org.junit.Assert._
import com.ml.graph.matcher.GraphRegExp.Matcher
import com.ee.core.common.{AnnotatedText, TextAnnotator}
import scala.collection.JavaConversions._

class AnnotationPipelineTest {
	val html1: String = "<html><n1>aaa<n11 hm='attr'>a<n111>a</n111></n11><n12>a</n12></n1><n2>aaa</n2><n3>aaa</n3></html>"

	import com.ml.crawler.pattern.annotation.AnnotationPipeline._

	@Test
	def pipe() {
		var matcher: Matcher = 'n1 % "part1" :: 'n2 % "part2"
		assertText(html1, regexp("\\w+", "token") /: pattern(matcher).setUseHtml(true),
			"part1[ aaa  a  a    a  ]\n\\n [[ aaa  a  a    a  ]]  aaa\npart2[ aaa ]\n a   [[ aaa ]]  aaa\n", "part1", "part2")

		matcher = {
			tag("n1") < tag("n11") :: tag("n12") % "part1"
		} :: tag("n2") % "part2"
		assertText(html1, regexp("\\w+", "token") /: pattern(matcher).setUseHtml(true),
			"part1[ a ]\n a   [[ a ]]   aa\npart2[ aaa ]\n a   [[ aaa ]]  aaa\n", "part1", "part2")

		matcher = {
			'n1 < 'n11 :: 'n12 % "part1"
		} :: 'n2 % "part2"
		assertText(html1, regexp("\\w+", "token") /: pattern(matcher).setUseHtml(true),
			"part1[ a ]\n a   [[ a ]]   aa\npart2[ aaa ]\n a   [[ aaa ]]  aaa\n", "part1", "part2")

		matcher = (tag("n1") < tag("n11") :: tag("n2")) % "all"
		assertText(html1, pattern(matcher).setUseHtml(true),
			"", "all")

		matcher = (tag("n1") < tag("n11")) :: tag("n2") % "all"
		assertText(html1, pattern(matcher).setUseHtml(true),
			"all[ aaa ]\n a   [[ aaa ]]  aaa\n", "all")
	}

	@Test
	def features() {
		val matcher = {
			any ? (m => {
				println("hm");
				m.first.attr('hm) == "attr"
			})
		} % "all"
		assertText(html1, pattern(matcher).setUseHtml(true),
			"all[ a  a  ]\n aaa [[ a  a  ]]  a  \n", "all")
	}

	@Test
	def tables() {
		import com.ml.crawler.metasite.FactMatchers._
		val tableHtmlText = """
		  <html>
		    <table>
				<tr><td>t1</td></tr>
			<tr><td>t1</td></tr>
			<tr><td>t1</td></tr>

			<tr><td>t1</td><td>t2</td></tr>
			<tr><td>t1</td><td>t2</td></tr>

			<tr><td>t1</td></tr>
		</table>
			</html>
			"""
		val text = assertText(tableHtmlText, pattern(Table()).setUseHtml(true),"", "tabdle")
		assertEquals("[ t 1  t 1  t 1 ][ t 1 ][ t 1  t 2  t 1  t 2 ]", text.getAnnotations.retrieve("table").map("["+_.getText().replaceAll("(?ms)\\s*"," ")+"]").mkString)
	}

	private def assertText(in: String, ann: TextAnnotator, result: String, cats: String*):AnnotatedText =  {

		val text = parseContent(in)
		println(text.getAnnotations(AnnotatedText.HTML_NAMESPACE).toSortedList.foreach(a=>println("%s[%s]" format (a.getType, a.getText))))
		execute(ann, text)
		val resultReal = text.getAnnotations.retrieve(Set(cats: _*)).toSortedList.map(_.getType).mkString
		assertEquals(result, text.toAnnotationsString(5, cats: _*))
		text
	}
}