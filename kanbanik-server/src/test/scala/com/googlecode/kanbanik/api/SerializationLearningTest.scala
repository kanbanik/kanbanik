package com.googlecode.kanbanik.api

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import net.liftweb.json._
import org.scalatest.FlatSpec

case class ClassWithOption(name: String, version: Option[Int])

@RunWith(classOf[JUnitRunner])
class SerializationLearningTest extends FlatSpec {
  implicit val formats = DefaultFormats // Brings in default date formats etc

  "serialization" should "work for simple case class" in {
    case class SomeClass(name: String, version: Int)

    val json = parse("""
         { "name": "Ignac",
           "version": 12
         }
    """)

    val extracted = json.extract[SomeClass]
    assert(extracted.name === "Ignac")
    assert(extracted.version === 12)
  }

  it should "work for list params" in {
    case class Param(name: String)
    case class SomeClass(params: List[Param])

    val json = parse("""
         { "params":
           [
             {
                "name": "name1"
             },
             {
                "name": "name2"
             }
           ]
         }
         """)

    val extracted = json.extract[SomeClass]
    assert(extracted.params.head.name === "name1")
    assert(extracted.params.tail.head.name === "name2")
  }


  it should "deal with incomplete arg list" in {
    val json = parse("""
         { "name": "Ignac"}
         """)

    val extracted = json.extract[ClassWithOption]
    assert(extracted.name === "Ignac")
    assert(extracted.version === None)
  }

  it should "be possible to query it using XPath" in {
    val json = parse("""
         { "name": "Ignac"}
                     """)

    val res = (json \ "name").extract[String]
    assert(res.toString === "Ignac")
  }

}
