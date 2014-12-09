package com.ml.crawler.pattern.xml

import org.jsoup.nodes.{Node => JSNode}

class MarkMatch (val name: String, val m: Match) extends CompositeMatch (m.getContext, m);
