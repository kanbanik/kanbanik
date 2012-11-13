package com.googlecode.kanbanik.commands;
import org.bson.types.ObjectId

import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.model.HasMongoConnection
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.model.Workflowitem
import com.googlecode.kanbanik.builders.WorkflowitemBuilder

class EditWorkflowitemDataCommand extends ServerCommand[SimpleParams[WorkflowitemDto], FailableResult[SimpleParams[WorkflowitemDto]]] with HasMongoConnection {
  
  val builder = new WorkflowitemBuilder
  
  def execute(params: SimpleParams[WorkflowitemDto]): FailableResult[SimpleParams[WorkflowitemDto]] = {
    val workflowitem = builder.buildEntity(params.getPayload())

    workflowitem.name = params.getPayload().getName()
    workflowitem.wipLimit = params.getPayload().getWipLimit()
    workflowitem.itemType = params.getPayload().getItemType().asStringValue()

    try {
    	new FailableResult(new SimpleParams(builder.buildDto(workflowitem.storeData)))
    } catch {
      case e: MidAirCollisionException =>
        return new FailableResult(new SimpleParams(params.getPayload()), false, ServerMessages.midAirCollisionException)
    }
  }
}
