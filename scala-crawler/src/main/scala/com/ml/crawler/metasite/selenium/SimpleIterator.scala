package com.ml.crawler.metasite.selenium


trait SimpleIterator[T] extends Iterator[T] {
  var current: T;
  var ready: Boolean = false

  def hasNext = {
    if (ready) {
      current != null
    } else {
      next ()
      hasNext
    }
  }

  def next () = {
    if (!ready) {
      ready = true;
      current = computeNext

    }
    current
  }

  def computeNext: T;
}
