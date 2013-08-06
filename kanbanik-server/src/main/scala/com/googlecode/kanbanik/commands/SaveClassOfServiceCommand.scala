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

    try {
      new FailableResult(new SimpleParams(classOfServiceBuilder.buildDto(entity.store)))
    } catch {
      case e: MidAirCollisionException =>
        new FailableResult(new SimpleParams, false, ServerMessages.midAirCollisionException)
    }
  }

}