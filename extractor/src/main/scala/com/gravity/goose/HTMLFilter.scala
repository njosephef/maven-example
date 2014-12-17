package com.gravity.goose

import java.io.File

import com.gravity.goose.utils.Filter
import org.apache.commons.io.FileUtils
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.native.Serialization

object HTMLFilter {
  /**
  * you can use this method if you want to run goose from the command line to extract html from a bashscript
  * or to just test it's functionality
  * you can run it like so
  * cd into the goose root
  * mvn compile
  * MAVEN_OPTS="-Xms256m -Xmx2000m"; mvn exec:java -Dexec.mainClass=com.gravity.goose.TalkToMeGoose -Dexec.args="http://techcrunch.com/2011/05/13/native-apps-or-web-apps-particle-code-wants-you-to-do-both/" -e -q > ~/Desktop/gooseresult.txt
  *
  * Some top gun love:
  * Officer: [in the midst of the MIG battle] Both Catapults are broken, sir.
  * Stinger: How long will it take?
  * Officer: It'll take ten minutes.
  * Stinger: Bullshit ten minutes! This thing will be over in two minutes! Get on it!
  *
  * @param args
  */

  def main(args: Array[String]) {
    try {

      implicit val formats = Serialization.formats(
        ShortTypeHints(
          List(
            classOf[TextContent],
            classOf[HTMLContent]
          )
        )
      )

      val fileName: String = args(0)
      val str = FileUtils readFileToString (new File(fileName), "UTF-8")
      val json = parse(str)
      val htmlContent = json.extract[HTMLContent]

      val config: Configuration = new Configuration
      config.enableImageFetching = false
      val filter = new Filter(config)
      val article = filter.filter(htmlContent.url, htmlContent.html);
      println(article.cleanedArticleText)

      val textContent = new TextContent(htmlContent.url, article.cleanedArticleText)

      FileUtils.write(new File("test.json"), Serialization.writePretty(textContent).toString, "UTF-8")
    }
    catch {
      case e: Exception => {
        System.out.println("Make sure you pass in a valid URL: " + e.toString)
      }
    }
  }
}


