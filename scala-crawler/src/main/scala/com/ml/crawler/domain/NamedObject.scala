package com.ml.crawler.domain


trait NamedObject extends DomainObject {
	var name: String = _
	var description: String = _
}