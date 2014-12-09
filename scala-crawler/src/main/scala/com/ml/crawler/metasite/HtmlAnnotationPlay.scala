package com.ml.crawler.metasite

import java.net.URL
import com.ml.crawler.domain.Restaurant
import com.ml.crawler.metasite._
import com.ee.core.common.AnnotatedText

object HtmlAnnotationPlay {

	import Facts._
	import com.ml.crawler.pattern.annotation.AnnotationPipeline._
	import Extractor._

	def main(str: Array[String]) {

		//iterate over special links
		//save links
		//go to each link page
		//get real link
		//get address
		//get telephone
		//download site

		val url = new URL("file:///D:/_projects/_lectures/results/Riga%20Restaurant%20Directory%20%20%20Riga-life.com.htm")
		val text = parseURL(url)

		val context = new Context()
		//

		//extract link in column(text(name)) and string in column(text(name)) in table(column('name'), column('type of place'), column())
		/*
		extract {
			Text as 'name in Column(Text("name"))) &&
			Text as 'descr in Column(Text("type of place"))
		}
		 */

		extract {
			((Text("") as 'name) && (Link("") as 'link) in Column(Text("name"))) and
				(Text("") as 'name in Column(Text("type of place")))
		}
		//


		val annot = pattern(
			'tr < {
				(('td < 'a % "name") ::
					'td % "description1" ::
					('td % "description2")) % {
					m =>
						val r = m.bind(new Restaurant(), {
							r: Restaurant =>
								r.description = m.get("description1").first.getText + " " + m.get("description2").first.getText
						})
						val url = m.get("name").first.attr('href)
						context add MoreInfoLink(r, Link(url))
				}
			}

		).setUseHtml(true)
		execute(annot, text)

		context.findAll[MoreInfoLink]().foreach {
			r: MoreInfoLink =>
				println("start processing " + r.link.url)
				val text: AnnotatedText = null; //parseURL(r.link.url)

			extract (Link("") as 'link before Text("website"))
			extract (Address("") as 'address after Text("address:"))
			////regexp("address:") :: address % 'phone
			extract (Phone("") as 'phone after Text("tel:")) //PhonePrefix
			//regexp("tel:") :: phone % 'phone
			extract (Text("") as 'descr in Paragraph() in MainBlock)
			//(text % 'name > paragraph) > mainBlock
			//extract link before "website"

			////extract Link(null) before Text("website")
			//extract text after address:
			//extract text after tel:
			//extract paragraph in mainblock
			//extract Text() in Paragraph() in MainBlock

			//http://www.delpopolo.lv/en/menu/breakfast
			extract {
				((Text("") as 'name in Column(1)) and (classOf[Money] as 'money in Column(2) )) in Table(columns = 2)
			}
			//(text % 'name > column(1)) :: (money % 'money > column(2)) > table(columns = 2)
			//(text % 'name > column(5)) :: (money % 'money > column(6)) > table(columns = 78)
			//with table(columns = 78) we annotate columns with number feature and row number) then we can use those annotations
		}

	}
}