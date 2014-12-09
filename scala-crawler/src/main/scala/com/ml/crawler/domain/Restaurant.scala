package com.ml.crawler.domain

import org.apache.commons.lang.builder.{ToStringStyle, ToStringBuilder}
import com.ml.crawler.metasite.Facts.Fact

case class Restaurant(var url: String = "") extends  NamedObject with Fact {
	var address: String = _
	var city: String = _
	var country: String = _
	//0..1
	var raiting: Double = _

	val menu: List[Menu] = List()

	class Menu(var cost: Money) extends NamedObject

	override def toString = ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)

}