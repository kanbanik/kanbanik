package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.ClassOfServiceBuilder
import com.googlecode.kanbanik.db.HasEntityLoader
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.dtos.{ErrorDto, EmptyDto, ClassOfServiceDto}
import com.googlecode.kanbanik.model.Board

class DeleteClassOfServiceCommand extends Command[ClassOfServiceDto, EmptyDto] with HasEntityLoader {

  lazy val classOfServiceBuilder = new ClassOfServiceBuilder

  def execute(params: ClassOfServiceDto): Either[EmptyDto, ErrorDto] = {
    val entity = classOfServiceBuilder.buildEntity(params)

    loadClassOfService(entity.id.get).getOrElse(
      return Right(ErrorDto(ServerMessages.entityDeletedMessage("class of service")))
    )

    // pretty heavy operation... Will need to be fixed when start to be slow
    val tasksOnClassOfService = for (board <- Board.all(true);
         task <- board.tasks;
         if (task.classOfService != null && task.classOfService.isDefined && task.classOfService.get.id.get == entity.id.get)
    ) yield task

    if (!tasksOnClassOfService.isEmpty) {
      Right(ErrorDto("The following tasks are assigned to this class of service: " + tasksOnClassOfService.map(_.ticketId).mkString(", ") + ". Please delete them first."))
    } else {
      entity.delete

      Left(EmptyDto())
    }
  }
  
}