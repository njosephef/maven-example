package com.ml.crawler.metasite


class SitePlay extends App {
  /*
  //simple scenario
  in Page("http://www.riga-life.com/eat/restaurant_directory.php") extract {
    'tr >: 'td >: 'a % "name" +: 'td % 'descr1 +: 'td % 'descr2
  } bind {
    m =>
      new Restaraunt(name = m("name").getText, description = m("descr1").getText)
  } saveTo("xml_db")

  //more complex
  process site (new Site("sdfdsf")) with {
    extractor(

    )
  }

  in page url1 extract urls under column name to Urls1
  for each url in Urls1
  getSingle(WebSiteUrl)
    process(site)  with {


   */

  //iterate over special links
  //save links
  //go to each link page
  //get real link
  //get address
  //get telephone
  //download site

  //new Page() extract(new Link()).foreach {
  //new Page().extract(new Object)
  //}
}