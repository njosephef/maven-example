package com.ml.crawler.html

import org.jsoup.Jsoup
import scala.collection.JavaConversions._
import org.jsoup.nodes._
import collection.mutable.ArrayBuffer
import com.ml.graph.matcher.GraphRegExp.{Match, Matcher}
import com.ml.crawler.pattern.xml._
import java.net.URL
import com.ml.crawler.MyUtil
import com.ml.crawler.pattern.xml.GExpr



object PlayWithSoup extends App {
  //	val url = "http://www.riga-life.com/eat/restaurant_directory.php"
  val url = "file:///D:/_projects/_lectures/results/Riga%20Restaurant%20Directory%20%20%20Riga-life.com.htm"
  println (url)
  val doc: Document = Jsoup.parse (new URL (url).openStream (), "UTF-8", "http://taimport com.ml.crawler.pattern.xml.GExpr._ort GExpr._
  //tr > ((td > a) :: td :: td :: td)
  val m: Matcher = tag ("tr") < (
      (tag ("td") << tag ("a") % "name") :: tag ("td") % "type" :: tag ("td") % "desc"
      )
  val list = new ArrayBuffer[InfoObject]();
  var count: Int = 0;

  case class InfoObject (val name: String, val typeDescr: String, val link: String)

  search (m, doc) {
    m: MatchAdapter =>
      println ("found")
      println (m.allElements.map (el => "%s[%s]" format (el.tagName (), el.text ())))
      count += 1;
      list += new InfoObject (m.get ("name").text,
        m.get ("name").text + " " + m.get ("type").text,
        m.get ("name").attr ("href")
      )
  }

  println (count)
  println ("stop matching")



  //
  //	val list = new ArrayBuffer[InfoObject]();
  //	doc.select("tr").foreach {
  //		e: Element =>
  //
  //			if (!e.select("td a").isEmpty && e.select("td").size() == 5) {
  //				//				println(e.html())
  //				//				println(e.children().size)
  //
  //				list += new InfoObject(e.select("td a").text(),
  //					e.select("td:eq(1)").text + " : " + e.select("td:eq(2)").text,
  //					e.select("td a").attr("href")
  //				)
  //			}
  //
  //	}
  println (list.mkString ("\n"))
  println (list.size)

  MyUtil.timer ("gexpr", 50) {
    search (m, doc) {
      m: MatchAdapter =>
        new InfoObject (m.get ("name").text,
          m.get ("name").text + " " + m.get ("type").text,
          m.get ("name").attr ("href")
        )
    }
  }


  MyUtil.timer ("old", 50) {
    search (m, doc) {
      m: MatchAdapter =>
        doc.select ("tr").foreach {
          e: Element =>

            if (!e.select ("td a").isEmpty && e.select ("td").size () == 5) {
              new InfoObject (e.select ("td a").text (),
                e.select ("td:eq(1)").text + " : " + e.select ("td:eq(2)").text,
                e.select ("td a").attr ("href")
              )
            }

        }
    }
  }
}