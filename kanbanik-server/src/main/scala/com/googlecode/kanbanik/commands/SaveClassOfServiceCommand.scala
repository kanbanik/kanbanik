package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.ClassOfServiceBuilder
import com.googlecode.kanbanik.dtos.{ErrorDto, ClassOfServiceDto}

class SaveClassOfServiceCommand extends Command[ClassOfServiceDto, ClassOfServiceDto] {

  lazy val classOfServiceBuilder = new ClassOfServiceBuilder()

  def execute(params: ClassOfServiceDto): Either[ClassOfServiceDto, ErrorDto] = {
    val entity = classOfServiceBuilder.buildEntity(params)

    Left(classOfServiceBuilder.buildDto(entity.store))
  }

}