package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.model.{User, ClassOfService}
import com.googlecode.kanbanik.builders.ClassOfServiceBuilder
import com.googlecode.kanbanik.dtos.{ErrorDto, EmptyDto, ClassOfServiceDto, ListDto}

class GetAllClassOfServices extends Command[EmptyDto, ListDto[ClassOfServiceDto]] {

  lazy val builder = new ClassOfServiceBuilder

  override def execute(params: EmptyDto, user: User): Either[ListDto[ClassOfServiceDto], ErrorDto] = {
    val dtos = ClassOfService.all(user).map(builder.buildDto)
    
    Left(ListDto(dtos))
  }
}