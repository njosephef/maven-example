package com.ml.crawler.metasite.selenium

import collection.mutable.ArrayBuffer
import org.openqa.selenium._
import firefox.FirefoxDriver
import com.ml.crawler.MyUtil
import java.util.concurrent.TimeUnit
import remote.DesiredCapabilities
import java.util.Arrays
import support.ui.WebDriverWait
import com.google.common.base.Predicate

object SileniumUtil extends App {

	implicit def by2bylike(b: By) = new ByLike(b)


	/*
	  * val forum = new GenericForum(x.screens)
	  * val catalogIterator = forum.catalog
	  * catlogIterator.find(_.text.contains("Roberts")).
	  * }
	  * ////////////////////////////////////////////
	  * checkpoints and persistence
	  * site analyze
	  * */


	def printText(ctx: Context) {
		println("------action--------")
		println(ctx.element.getText)
		println(ctx.element)
	}

	val rigaRestaraunt = {
		val by1 = By.cssSelector(".details")
		goto("http://www.tripadvisor.com/Restaurants-g274967-Riga.html") :: //listing
			(Current + nextLink(By.className("sprite-pageNext")).doWait(10000)) :: elements(by1) :: execute(printText)
	}

	val rigaAll = {
		goto("http://www.riga-life.com/") :: links(By.cssSelector("#nav a") withRegexp "\\d+") ::
			elements(By.cssSelector("#maincontent td a")) :: execute(printText)

	}

	val yandex = goto("http://www.yandex.ru/").log("start") :: doActionsWithDriver(d => {
		val el = d.findElement(By.id("text"))
		el.sendKeys("java")
		el.submit()
	}) :: (Current + nextLink(By.id("next_page")).log("next-page").doWait(1000)).slice(5, 10) :: elements(By.cssSelector(".b-serp-item__title-link")).log("elements") :: execute(printText)

	MyUtil.timer("extract riga data") {
		val driver = new FirefoxDriver()
		val caps = DesiredCapabilities.chrome();
		caps.setCapability("chrome.binary", "D:\\Documents and Settings\\Lurii.Korolov\\Local Settings\\Application Data\\Google\\Chrome\\Application\\chrome.exe")
		val chomeDriver = "d:\\_projects\\_lectures\\qew\\chromedriver.exe"
		caps.setCapability("webdriver.chrome.driver", chomeDriver)
		caps.setCapability("chrome.switches", Arrays.asList("--disable-images=true"));
		System.setProperty("webdriver.chrome.driver", chomeDriver)
		//		val driver = new ChromeDriver(caps);

		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS)
		driver.manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS)
		//		rigaRestaraunt.execute(driver)
		rigaAll.execute(driver)
	}


	/////////////////
	type CIF = ContextIteratorFactory
	type IT = Traversable[Context]

	trait ContextIterator extends Iterator[Context] {
		private type UndoAction = (() => Unit)
		private val list = new ArrayBuffer[UndoAction]()

		def registerUndo(undo: UndoAction) {
			list += undo
		}

		def undo() {
			list.reverse.foreach(_())
			list.clear()
		}
	}

	trait ContextIteratorFactory {
		self =>

		def pipe(ci: CIF): CIF = transform(it => new IT {
			def foreach[U](f: (Context) => U) {
				it.foreach(ci.createIterator(_).foreach(f))
			}
		})

		def ::(ci: CIF) = ci.pipe(this)

		def +(ci: CIF) = this.join(ci)

		def slice(from: Int, to: Int) = transform(it => new IT {
			def foreach[U](f: (Context) => U) = {
				var pos = 0;
				try {
					it.foreach {
						c: Context =>
							if (pos >= to) throw new StopException
							val res = if (pos >= from) f(c) else null
							pos += 1;
							res
					}
				} catch {
					case s: StopException => null
				}
			}


		})

		def take(n: Int) = slice(0, n)

		def drop(n: Int) = slice(n, Int.MaxValue)

		def doWait(mils: Long) = transform(it => new IT {
			def foreach[U](f: (Context) => U) = it.foreach {
				println("do wait " + mils)
				MyUtil.doWait(mils)
				f(_)
			}
		})

		def log(name: String) = transform(it => new IT {
			def foreach[U](f: (Context) => U) {
				def state(cur: Context): String = if (cur == null) {
					"null"
				} else {
					"url=%s, element=%s" format (cur.driver.getCurrentUrl, if (cur.element != null) cur.element.getTagName + ":" + cur.element.getText else "null")
				}
				println(name + "(start)")
				var count = 0
				it.foreach {
					c =>
						f(c)
						count += 1
						println("%s(after) : %s" format (name, state(c)))
				}
				println(name + "(end)/" + count)
			}
		})

		//impl
		def join(ci: CIF): CIF = new CIF {
			def createIterator(ctx: Context) = new IT {
				val i1 = self.createIterator(ctx)
				val i2 = ci.createIterator(ctx)

				def foreach[U](f: (Context) => U) {
					i1.foreach(f)
					i2.foreach(f)
				}
			}
		}

		def transform(func: (IT => IT)) = new CIF {
			def createIterator(ctx: Context): IT = func(self.createIterator(ctx))
		}

		def createIterator(ctx: Context): IT

		def execute(wd: WebDriver) {
			var counter = 0
			createIterator(new Context {
				def driver = wd
			}).foreach(_ => counter += 1)
			println("counter = " + counter)
		}
	}


	def links(by: By): CIF = new CIF() {
		def createIterator(ctx: Context) = new IT {
			def foreach[U](f: (Context) => U) {
				var pos = 0
				def nextElement = new ByContextImpl(by, pos)(ctx.driver)
				new UndoActions().withUndo {
					undo =>
						while (nextElement.element != null) {
							val ctx = nextElement
							ctx.element.click()
							undo.registerUndo(() => ctx.driver.navigate().back())
							f(ctx)
							undo.doUndo()
							pos += 1
						}
				}
			}
		}


	}


	def nextLink(by: By): CIF = new CIF() {
		def createIterator(ctx: Context) = new IT {
			def foreach[U](f: (Context) => U) {
				def nextElement = new ByContextImpl(by, 0)(ctx.driver)
				new UndoActions().withUndo {
					undo =>
						while (nextElement.element != null) {
							val ctx = nextElement
							ctx.element.click()
							undo.registerUndo(() => ctx.driver.navigate().back())
							f(ctx)
						}
				}
			}
		}
	}

	lazy val Current: CIF = new CIF {
		def createIterator(ctx: Context) = Traversable(ctx)
	}


	def elements(by: By): CIF = new CIF() {
		def createIterator(ctx: Context) = {
			new IT {
				def foreach[U](f: (Context) => U) {
					var pos = 0
					def nextElement = new ByContextImpl(by, pos)(ctx.driver)
					while (nextElement.element != null) {
						val ctx = nextElement
						f(ctx)
						pos += 1
					}
				}
			}


		}
	}

	def element(by: By) = elements(by).take(1)


	def goto(url: String, withUndo: Boolean = false): CIF = new CIF {
		def createIterator(ctx: Context) = TraversableOne {
			undo =>
				if (withUndo) {
					val undoLink = ctx.driver.getCurrentUrl
					undo.registerUndo(() => ctx.driver.get(undoLink))
				}
				ctx.driver.get(url)
				new Context {
					def driver = ctx.driver
				}
		}
	}

	def execute(action: (Context => Unit)): CIF = new CIF {
		def createIterator(ctx: Context) = {
			//TODO: no rollback supported!!!
			action(ctx);
			Traversable(ctx)
		}
	}

	def doActionsWithDriver(action: WebDriver => Unit) = execute(ctx => action(ctx.driver))

	def doWaitUntil(func: Context => Boolean, maxTime: Long = Long.MaxValue) = new CIF {
		def createIterator(ctx: Context) = {
			//				MyUtil.waitUntil(func(ctx), maxTime)
			new WebDriverWait(ctx.driver, maxTime).until(new Predicate[WebDriver]() {
				def apply(input: WebDriver) = func(ctx)
			})
			Traversable(ctx)
		}
	}


	def fromFunction(action: (Context => IT)): CIF = new CIF {
		def createIterator(ctx: Context): IT = action(ctx)
	}

	object Condition {
		def exist(by: By, atLeast: Int = 1)(ctx: Context): Boolean = {
			println("waiting for " + by)
			ctx.searchContext.findElements(by).size() > atLeast
		}
	}


	trait Context {
		def driver: WebDriver

		def element: WebElement = null

		def isEmpty = element == null

		def searchContext: SearchContext = if (!isEmpty) element else driver
	}

	trait ByContext extends Context {
		val by: By
		val pos: Int

		def searchContextForElement: SearchContext = driver

		override def element = {
			if (pos == 0) {
				try {
					searchContextForElement.findElement(by)
				} catch {
					case e: NoSuchElementException => {
						returnNull
					}
				}
			} else {
				val list = searchContextForElement.findElements(by)
				if (pos < list.size()) list.get(pos)
				else returnNull
			}
		}

		private def returnNull: WebElement = {
			println("no value by %s[%d]" format (by, pos))
			null
		}
	}

	class ByContextImpl(val by: By, val pos: Int)(implicit val driver: WebDriver) extends ByContext

	class StopException extends Throwable

	class UndoActions {
		type Func = (() => Unit)
		private val undos = new ArrayBuffer[Func]()

		def registerUndo(func: Func) = undos += func

		def doUndo() {
			undos.reverse.foreach(_())
			undos.clear()
		}

		def withUndo(func: UndoActions => Unit) {
			try {
				func(this)
			} finally {
				doUndo()
			}
		}
	}

	class ByLike(by: By) {

		import scala.collection.JavaConversions._

		def withPredicate(pred: WebElement => Boolean): By = new By {
			def findElements(context: SearchContext) = context.findElements(by).toList.filter(pred)

			override def findElement(context: SearchContext) = context.findElements(by).toIterator.find(pred) match {
				case Some(s) => s
				case None => null
			}

			override def toString = by.toString + ".withPredicate["+pred.toString()+"]"
		}

		def withText(str: String) = withPredicate(_.getText.contains(str))

		def withRegexp(str: String) = withPredicate(el => str.r.findFirstIn(el.getText).isDefined)
	}


	def TraversableOne(contextFactory: UndoActions => Context) = new IT {
		def foreach[U](f: (Context) => U) {
			new UndoActions().withUndo(undo => f(contextFactory(undo)))
		}
	}


}