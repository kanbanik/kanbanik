package com.googlecode.kanbanik

import java.util.ArrayList
package object commons {

  type JavaList[T] = java.util.List[T]

  class ToJavaConvertableList[T](list: List[T]) {
    def toJavaList(): JavaList[T] = {
      val javaList = new ArrayList[T]
      list.foreach(javaList.add(_))
      javaList
    }
  }
  
  class ToScalaConvertableList[T](list: JavaList[T]) {
    def toScalaList(): List[T] = {
      list.toArray().toList.asInstanceOf[List[T]]
    }
  }

  implicit def makeToJavaConvertableList[T](scalaList: List[T]): ToJavaConvertableList[T] = new ToJavaConvertableList[T](scalaList)
  
  implicit def makeToScalaConvertableList[T](javaList: JavaList[T]): ToScalaConvertableList[T] = new ToScalaConvertableList[T](javaList)

}
