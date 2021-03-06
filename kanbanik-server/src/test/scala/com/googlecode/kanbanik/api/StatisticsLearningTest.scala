package com.googlecode.kanbanik.api

import java.util

import com.googlecode.kanbanik.commands.ExecuteStatisticsCommand
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.kanbanik.statistics
import org.scalatest.FlatSpec
import net.liftweb.json._
import com.googlecode.kanbanik.dtos._

@RunWith(classOf[JUnitRunner])
class StatisticsLearningTest extends FlatSpec {
  implicit val formats = DefaultFormats

  "statistics" should "be possible to call it" in {
    true
//    val fullDescriptor = new util.HashMap[String, Object]()
//    val resultDescriptor = new util.HashMap[String, Object]()
//    val filterType = new util.HashMap[String, Object]()
//    val resultDescriptors = new util.ArrayList[Object]()
//
//    fullDescriptor.put(":reduce-function", ":merge")
//    filterType.put(":eventType", "TaskDeleted")
//    resultDescriptor.put(":function", ":cnt")
//    resultDescriptor.put(":filter", filterType)
//    resultDescriptors.add(resultDescriptor)
//    fullDescriptor.put(":result-descriptors", resultDescriptors)
////
////    {:reduce-function :merge
////      :result-descriptors [{
////      :filter {:eventType "TaskDeleted"}
////      :function :pass
////    }]}
//
//    val x = statistics.execute(fullDescriptor, 3600000)
//    print(x)

  }

  it should "should parse the data properly" in {
    val json = parse("""
                           {"reduceFunction": ":merge",
                            "timeframe": 100,
                             "resultDescriptors": [
                             {
                              "filter": {"example": {"eventType": "TaskDeleted"}}
                             }
                           ]}
                     """)
    val res = json.extract[AnalyzeDescriptorDto]
    assert(res.reduceFunction === ":merge")

    val cmd = new ExecuteStatisticsCommand()

    val x = statistics.execute(cmd.toJDescriptor(res), 600000)
    print(x)
  }

}

