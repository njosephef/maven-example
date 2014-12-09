package com.ml.crawler.domain


class Money(var amount:Double)  {
	val currency:Currency = null
	def convert(cur:Currency) = new Money(amount)
}
class Currency(val name:String)
class USD extends Currency("USD")
class LAT extends Currency("LAT")