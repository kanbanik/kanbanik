package com.googlecode.kanbanik.commands

import java.util

import com.googlecode.kanbanik.dtos._
import com.googlecode.kanbanik.model.User

import org.kanbanik.statistics

class ExecuteStatisticsCommand extends Command[AnalyzeDescriptorDto, AnalyzeResultDto] {

  override def execute(params: AnalyzeDescriptorDto, user: User): Either[AnalyzeResultDto, ErrorDto] = {
    val res = statistics.execute(toJDescriptor(params))
    Left(AnalyzeResultDto(res))
  }

  def toJDescriptor(data: AnalyzeDescriptorDto): java.util.Map[String, Object] = {
    val result = new util.HashMap[String, Object]()

    result.put(":reduce-function", data.reduceFunction)
    result.put(":result-descriptors", toJResultDescriptors(data.resultDescriptors))

    result
  }

  def toJResultDescriptors(descriptors: List[AnalyzeResultDescriptor]): java.util.List[Object] = {
    val jres = new util.ArrayList[Object]()

    for (descriptor <- descriptors) {
      val jdescriptor = new java.util.HashMap[String, Object]()
      jdescriptor.put(":function", descriptor.function)
      val jfilter = new java.util.HashMap[String, Object]()

      for ((k, v) <- descriptor.filter) {
        jfilter.put(":" + k, v)
      }

      jdescriptor.put(":filter", jfilter)
      jres.add(jdescriptor)
    }
    jres
  }

}
