package com.ml.crawler.metasite.selenium

import java.util.Arrays
import java.util.concurrent.TimeUnit

import com.ml.crawler.MyUtil

/**
 * to will remove when it be clear that with traversable it works better
 */
@deprecated
object SileniumUtilIterable extends App {

  /*
    * val forum = new GenericForum(x.screens)
    * val catalogIterator = forum.catalog
    * catlogIterator.find(_.text.contains("Roberts")).
    * }
    * */

  //	val action = execute {
  //		ctx: Context => println(ctx.element.getText)
  //	}
  //	val iter2 = goto("http://x-screen.com") ::
  //		links(By.xpath("all topic level1")) ::
  //		links(By.xpath("all topic level2")) ::
  //		execute(_ => println("hm")) || link(By.xpath("next page link")) :: action


  def printText (ctx: Context) {
    println ("------action--------")
    println (ctx.element.getText)
    println (ctx.element)
  }

  val rigaRestaraunt = {
    val by1 = By.cssSelector (".details")
    goto ("http://www.tripadvisor.com/Restaurants-g274967-Riga.html").log ("start") :: //listing
        (Current + nextLink (By.className ("sprite-pageNext")).log ("next page").doWait (10000)) :: elements (by1) :: execute (printText)
  }

  val yandex = goto ("http://www.yandex.ru/") :: doActionsWithDriver (d => {
    val el = d.findElement (By.id ("text"))
    el.sendKeys ("java")
    el.submit ()
  }) :: (Current + nextLink (By.id ("next_page")).doWait (1000)) :: elements (By.cssSelector (".b-serp-item__title-link")) :: execute (printText)

  MyUtil.timer ("extract riga data") {
    val driver = new FirefoxDriver ()
    val caps = DesiredCapabilities.chrome ();
    caps.setCapability ("chrome.binary", "D:\\Documents and Settings\\Lurii.Korolov\\Local Settings\\Application Data\\Google\\Chrome\\Application\\chrome.exe")
    val chomeDriver = "d:\\_projects\\_lectures\\qew\\chromedriver.exe"
    caps.setCapability ("webdriver.chrome.driver", chomeDriver)
    caps.setCapability ("chrome.switches", Arrays.asList ("--disable-images=true"));
    System.setProperty ("webdriver.chrome.driver", chomeDriver)
    //		val driver = new ChromeDriver(caps);

    driver.manage ().timeouts ().implicitlyWait (5, TimeUnit.SECONDS)
    driver.manage ().timeouts ().setScriptTimeout (5, TimeUnit.SECONDS)
    //		rigaRestaraunt.execute(driver)
    yandex.execute (driver)
  }


  /////////////////
  type CIF = ContextIteratorFactory
  type IT = Iterator[Context]

  trait ContextIterator extends Iterator[Context] {
    private type UndoAction = (() => Unit)
    private val list = new ArrayBuffer[UndoAction]()

    def registerUndo (undo: UndoAction) {
      list += undo
    }

    def undo () {
      list.reverse.foreach (_ ())
      list.clear ()
    }
  }

  trait ContextIteratorFactory {
    self =>

    def foreach (ci: CIF): CIF = transform (it => new IT {
      def hasNext = it.hasNext

      def next () = {
        val value = it.next ()
        ci.createIterator (value).foreach (Function.const (null))
        value
      }
    })

    def :: (ci: CIF) = ci.foreach (this)

    def + (ci: CIF) = this.join (ci)

    def slice (from: Int, to: Int) = transform (_.slice (from, to))

    def take (n: Int) = slice (0, n)

    def drop (n: Int) = slice (n, Int.MaxValue)

    def doWait (mils: Long) = transform (it => new IT {
      def hasNext = it.hasNext

      def next () = {
        val r = it.next
        MyUtil.doWait (mils)
        r
      }
    })

    def log (name: String) = transform (it => new IT {
      def hasNext = {
        val res = it.hasNext
        if (!res && cur == null) println (name + " : is empty")
        res
      }

      var cur: Context = null

      def next () = {
        def state: String = if (cur == null) {
          "null"
        } else {
          "url=%s, element=%s" format (cur.driver.getCurrentUrl, if (cur.element != null) cur.element.getTagName + ":" + cur.element.getText else "null")
        }
        println ("%s(before next) : %s" format (name, state))
        cur = it.next ()
        println ("%s(after next) : %s" format (name, state))
        cur
      }
    })

    //impl
    def join (ci: CIF): CIF = new CIF {
      def createIterator (ctx: Context) = self.createIterator (ctx) ++ ci.createIterator (ctx)
    }

    def transform (func: (IT => IT)) = new CIF {
      def createIterator (ctx: Context): IT = func (self.createIterator (ctx))
    }

    def createIterator (ctx: Context): IT

    def execute (wd: WebDriver) {
      var counter = 0
      createIterator (new Context {
        def driver = wd
      }).foreach (_ => counter += 1)
      println ("counter = " + counter)
    }
  }


  def links (by: By): CIF = new CIF () {
    def createIterator (ctxParent: Context) = new ByContextIterator {
      var ctx: ByContext = new ByContextImpl (by, -1)(ctxParent.driver)

      protected def nextElement = new ByContextImpl (by, ctx.pos + 1)(ctx.driver)

      override def next () = {
        ctx = nextElement
        ctx.element.click ()
        //				registerUndo(ctx.driver.navigate().back())
        ctx
      }
    }


  }


  def nextLink (by: By): CIF = new CIF () {
    def createIterator (ctxParent: Context) = new ByContextIterator {
      var ctx: ByContext = new ByContextImpl (by, 0)(ctxParent.driver)

      protected def nextElement = ctx

      override def next () = {
        ctx = nextElement
        ctx.element.click ()
        ctx
      }
    }
  }

  lazy val Current: CIF = new CIF {
    def createIterator (ctx: Context) = Iterator (ctx)
  }


  def elements (by: By): CIF = new CIF () {
    def createIterator (ctxParent: Context) = {
      class ElContext (val by: By, val pos: Int) extends ByContext {
        def driver = ctxParent.driver

        //				override def searchContextForElement = ctxParent.searchContext
      }
      new ByContextIterator {
        var ctx: ByContext = new ElContext (by, -1)

        protected def nextElement = new ElContext (by, ctx.pos + 1)
      }
    }
  }

  def element (by: By) = elements (by).take (1)


  def goto (url: String): CIF = new CIF {
    def createIterator (ctx: Context) = {
      val undoLink = ctx.driver.getCurrentUrl
      ctx.driver.get (url)
      Iterator (new Context {
        def driver = ctx.driver
      })
    }
  }

  def execute (action: (Context => Unit)): CIF = new CIF {
    def createIterator (ctx: Context) = {
      action (ctx);
      Iterator (ctx)
    }
  }

  def doWaitUntil (func: Context => Boolean, maxTime: Long = Long.MaxValue) = new CIF {
    def createIterator (ctx: Context) = {
      //				MyUtil.waitUntil(func(ctx), maxTime)
      new WebDriverWait (ctx.driver, 10).until (new Predicate[WebDriver]() {
        def apply (input: WebDriver) = func (ctx)
      })
      Iterator (ctx)
    }
  }

  def doActionsWithDriver (action: WebDriver => Unit) = new CIF {
    def createIterator (ctx: Context) = {
      action (ctx.driver);
      Iterator (ctx)
    }
  }


  def fromFunction (action: (Context => IT)): CIF = new CIF {
    def createIterator (ctx: Context): IT = action (ctx)
  }

  object Condition {
    def exist (by: By, atLeast: Int = 1)(ctx: Context): Boolean = {
      println ("waiting for " + by)
      ctx.searchContext.findElements (by).size () > atLeast
    }
  }


  trait Context {
    def driver: WebDriver

    def element: WebElement = null

    def isEmpty = element == null

    def undo (): Unit = {}

    def searchContext: SearchContext = if (!isEmpty) element else driver
  }

  trait ByContext extends Context {
    val by: By
    val pos: Int

    def searchContextForElement: SearchContext = driver

    override def element = {
      if (pos == -12) {
        try {
          searchContextForElement.findElement (by)
        } catch {
          case e: NoSuchElementException => {
            returnNull
          }
        }
      } else {
        val list = searchContextForElement.findElements (by)
        if (pos < list.size ()) list.get (pos)
        else returnNull
      }
    }

    private def returnNull: WebElement = {
      println ("no value by %s[%d]" format (by, pos))
      null
    }
  }

  class ByContextImpl (val by: By, val pos: Int)(implicit val driver: WebDriver) extends ByContext


  private abstract class ByContextIterator extends IT {
    protected var ctx: ByContext

    protected def nextElement: ByContext

    def hasNext = nextElement.element != null

    def next () = {
      ctx = nextElement
      ctx
    }
  }

  private trait ClosableIterator extends IT {

    def beforeAll {}

    def afterIteration {}

    def afterAll {}

  }

}