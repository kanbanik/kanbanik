package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.ClassOfServiceDto
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.builders.ClassOfServiceBuilder
import com.googlecode.kanbanik.db.HasEntityLoader
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.model.ClassOfService
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.exceptions.MidAirCollisionException

class DeleteClassOfServiceCommand extends ServerCommand[SimpleParams[ClassOfServiceDto], FailableResult[VoidParams]] with HasEntityLoader {

  val classOfServiceBuilder = new ClassOfServiceBuilder

  def execute(params: SimpleParams[ClassOfServiceDto]): FailableResult[VoidParams] = {
    val entity = classOfServiceBuilder.buildEntity(params.getPayload)
    val dbEntity = loadClassOfService(entity.id.get).getOrElse(return new FailableResult(new VoidParams(), false, ServerMessages.entityDeletedMessage("class of service")))
    
    try {
    	entity.delete
    } catch {
      case e: MidAirCollisionException =>
        return new FailableResult(new VoidParams(), false, ServerMessages.midAirCollisionException)
    }
    
    return new FailableResult(new VoidParams)
  }
  
  // as soon as the assign to task will be done
  def canBeDeleted(classOfService: ClassOfService) {
    if (!classOfService.isPublic) {
      // good case, only the board's tasks has to be looked up
//      val board = loadBoard(classOfService.board.id.get, true).get
//      board.tasks.filter(task => task.classOfService)
    }
  }
}