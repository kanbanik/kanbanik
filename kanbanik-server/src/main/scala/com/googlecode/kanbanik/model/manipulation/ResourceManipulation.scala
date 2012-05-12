package com.googlecode.kanbanik.model.manipulation

trait ResourceManipulation {
  def using[A <: { def close(): Unit }, B](param: A)(f: A => B): B =
    try {
      f(param)
    } finally {
      param.close()
    }
}