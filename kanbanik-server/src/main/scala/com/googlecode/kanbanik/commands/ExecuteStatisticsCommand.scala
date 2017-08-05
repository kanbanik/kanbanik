package com.googlecode.kanbanik.commands

import java.util

import com.googlecode.kanbanik.dtos._
import com.googlecode.kanbanik.model.User

class ExecuteStatisticsCommand extends Command[AnalyzeDescriptorDto, AnalyzeResultDto] {

  override def execute(params: AnalyzeDescriptorDto, user: User): Either[AnalyzeResultDto, ErrorDto] = {
    ???
  }

  def toJavaParams(data: AnalyzeDescriptorDto): java.util.Map[String, Object] = {
    val result = new util.HashMap[String, Object]()
    val resultDescriptors = new util.ArrayList[Object]()

    result.put(":reduce-function", data.reduceFunction)

    result
  }

  def prepareDescriptors(descriptors: List[AnalyzeResultDescriptor]): java.util.List[Object] = {
    val res = new util.ArrayList[Object]()

    for (descriptor <- descriptors) {
      val jdescriptor = new java.util.HashMap[String, Object]()
      jdescriptor.put(":function", descriptor.function)
      val jfilter = new java.util.HashMap[String, Object]()
//      jfilter.put(":" + )
      jdescriptor.put(":filter", descriptor.filter)
    }
    res
  }

}
