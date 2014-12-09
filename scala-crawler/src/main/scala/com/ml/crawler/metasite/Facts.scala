package com.ml.crawler.metasite

import java.net.URL


object Facts {

	trait Fact;

	case class Link(urlString: String) extends Fact {
		def url = new URL(urlString)
	}

	case class MoreInfoLink(f: Fact, link: Link) extends Fact

	case class Text(text: String) extends Fact

	case class Phone(phone: String) extends Fact

	case class Email(phone: String) extends Fact

	case class Address(phone: String) extends Fact
	case class Money(d:Double, currency:String) extends Fact

	case class Person(phone: String) extends Fact
	case class Product(phone: String) extends Fact
	case class Company(phone: String) extends Fact

}