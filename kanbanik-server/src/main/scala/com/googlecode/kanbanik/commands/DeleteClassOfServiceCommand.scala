package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.ClassOfServiceBuilder
import com.googlecode.kanbanik.db.HasEntityLoader
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.dtos.{ErrorDto, EmptyDto, ClassOfServiceDto}

class DeleteClassOfServiceCommand extends Command[ClassOfServiceDto, EmptyDto] with HasEntityLoader {

  lazy val classOfServiceBuilder = new ClassOfServiceBuilder

  def execute(params: ClassOfServiceDto): Either[EmptyDto, ErrorDto] = {
    val entity = classOfServiceBuilder.buildEntity(params)

    loadClassOfService(entity.id.get).getOrElse(
      return Right(ErrorDto(ServerMessages.entityDeletedMessage("class of service")))
    )

    entity.delete
    
    return Left(EmptyDto())
  }
  
}