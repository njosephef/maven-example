package com.ml.crawler.pattern

import java.lang.reflect.Method


object Binder {
	implicit def reflector(ref: AnyRef) = new {
		private def methods(all:Boolean) = {
			def getMethods(cls: Class[_]): Stream[Method] =
				if (cls != classOf[Object]) cls.getMethods.toStream #::: getMethods(cls.getSuperclass)
				else Stream.empty
			getMethods(ref.getClass)
		}

		def getV[T](name: String, all:Boolean=false): T = {
			val m = methods(all).find(m => m.getName == name || m.getName.equalsIgnoreCase("get" + name)).get
			if(all)m.setAccessible(true)
			m.invoke(ref).asInstanceOf[T]
		}


		def setV(name: String, value: Any): Unit = {
			val prop = methods(false).find(_.getName == name + "_$eq")
			if (prop.isDefined) {
				prop.get.invoke(ref, value.asInstanceOf[AnyRef])
			} else {
				println("property %s undefined in class %s" format (name, ref.getClass.getName))
			}
		}
	}
}