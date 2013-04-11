package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.ClassOfServiceDto
import com.googlecode.kanbanik.builders.ClassOfServiceBuilder
import com.googlecode.kanbanik.db.HasEntityLoader
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.messages.ServerMessages

class SaveClassOfServiceCommand extends ServerCommand[SimpleParams[ClassOfServiceDto], FailableResult[SimpleParams[ClassOfServiceDto]]] with HasEntityLoader {

  lazy val classOfServiceBuilder = new ClassOfServiceBuilder()
  
  def execute(params: SimpleParams[ClassOfServiceDto]): FailableResult[SimpleParams[ClassOfServiceDto]] = {
    val entity = classOfServiceBuilder.buildEntity(params.getPayload())
    val boardId = entity.board.id.getOrElse(return new FailableResult(params, false, "The board has to be set!"))
    loadBoard(boardId, false).getOrElse(return new FailableResult(params, false, "The board with which this class of service is associated does not exist anymore. Possibly it was deleted by a different user"))
    
    try {
      new FailableResult(new SimpleParams(classOfServiceBuilder.buildDto(entity.store)))
    } catch {
      case e: MidAirCollisionException =>
        new FailableResult(new SimpleParams, false, ServerMessages.midAirCollisionException)
    }
  }
  
}