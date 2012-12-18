package com.googlecode.kanbanik.commands;
import org.bson.types.ObjectId

import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.db.HasMongoConnection
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.model.Workflowitem
import com.googlecode.kanbanik.builders.WorkflowitemBuilder

class EditWorkflowitemDataCommand extends ServerCommand[SimpleParams[WorkflowitemDto], FailableResult[SimpleParams[WorkflowitemDto]]] with HasMongoConnection {
  
  val builder = new WorkflowitemBuilder
  
  def execute(params: SimpleParams[WorkflowitemDto]): FailableResult[SimpleParams[WorkflowitemDto]] = {
    val workflowitem = builder.buildEntity(params.getPayload(), None, None)

    val name = params.getPayload().getName()
    val wipLimit = params.getPayload().getWipLimit()
    val itemType = params.getPayload().getItemType().asStringValue()
    
    val updated = workflowitem.withName(name).withWipLimit(wipLimit).withItemType(itemType)
    
    try {
    	new FailableResult(new SimpleParams(builder.buildDto(updated.store, None)))
    } catch {
      case e: MidAirCollisionException =>
        return new FailableResult(new SimpleParams(params.getPayload()), false, ServerMessages.midAirCollisionException)
    }
  }
}
