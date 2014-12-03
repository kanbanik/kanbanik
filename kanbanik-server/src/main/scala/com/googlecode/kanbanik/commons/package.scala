package com.googlecode.kanbanik

import java.util.ArrayList
import com.mongodb.DBObject

package object commons {

  type JavaList[T] = java.util.List[T]

  class ToJavaConvertableList[T](list: List[T]) {
    def toJavaList: JavaList[T] = {
      val javaList = new ArrayList[T]
      list.foreach(javaList.add)
      javaList
    }
  }

  class ToScalaConvertableList[T](list: JavaList[T]) {
    def toScalaList: List[T] = {
      list.toArray.toList.asInstanceOf[List[T]]
    }
  }

  class ToDbWithDefault(dbObject: DBObject) {
    def getWithDefault[T](key: Any, default: T): T = {
      val res = dbObject.get(key.toString)
        if (res != null) {
          res.asInstanceOf[T]
        } else {
          default
        }
    }
  }

  implicit def makeToJavaConvertableList[T](scalaList: List[T]): ToJavaConvertableList[T] = new ToJavaConvertableList[T](scalaList)

  implicit def makeToScalaConvertableList[T](javaList: JavaList[T]): ToScalaConvertableList[T] = new ToScalaConvertableList[T](javaList)

  implicit def makeToWithDefault(dbObject: DBObject): ToDbWithDefault = new ToDbWithDefault(dbObject)
}
