package com.googlecode.kanbanik.model.manipulation

trait ResourceManipulation {
  def using[A <: { def close(): Unit }, B](param: A)(f: A => B): B =
    try {
      f(param)
    } finally {
      // do not close it - this is not a connection, this is a pool
      // of connections. It will be closed at shutdown of the web
      // container using a listener class
    }
}