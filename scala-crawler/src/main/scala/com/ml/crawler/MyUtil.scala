package com.ml.crawler


object MyUtil {
	def timer(name: String, count: Int = 1)(func: => Unit): Long = {
		val start = System.currentTimeMillis()
		(1 to count).foreach(i => func)
		val end = System.currentTimeMillis()
		println("%s %ds" format (name, (end - start)))
		(end - start)
	}

	def check[T](value: T, checker: (T => Boolean)): T = {
		if (!checker(value)) {
			assert(false)
		}
		value
	}

	def checkNonEmpty[T <: TraversableOnce[_]](value: T): T = check(value, {
		c: T => c != null && c.nonEmpty
	})


	def doWait(time: Long) {
//		val lock = new Object()
//		lock.synchronized {
//			lock.wait(time)
//		}
		Thread.sleep(time)
	}

	def waitUntil(cond: => Boolean, maxTime: Long = Long.MaxValue): Boolean = {
		def cur = System.currentTimeMillis()
		val start = cur
		while (!cond && (cur - start) < maxTime) {
			doWait(100)
		}
		cond
	}

	//	def reverseEval[T](last: =>T, other:(=> Unit)* ) {
	//
	//	}
}