package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.model.ClassOfService
import com.googlecode.kanbanik.builders.ClassOfServiceBuilder
import com.googlecode.kanbanik.dtos.{ErrorDto, EmptyDto, ClassOfServiceDto, ListDto}

class GetAllClassOfServices extends Command[EmptyDto, ListDto[ClassOfServiceDto]] {

  lazy val builder = new ClassOfServiceBuilder

  def execute(params: EmptyDto): Either[ListDto[ClassOfServiceDto], ErrorDto] = {
    val dtos = ClassOfService.all.map(builder.buildDto2(_))
    
    Left(ListDto(dtos))
  }
}