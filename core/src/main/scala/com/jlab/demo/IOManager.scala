package com.jlab.demo

import scala.reflect.io.Path
import scala.util.Try

/**
 * Created by scorpiovn on 11/30/14.
 */
object IOManager {
  def delete(dir: String) {
    val path: Path = Path(dir)
    Try(path.deleteRecursively())
  }
}
