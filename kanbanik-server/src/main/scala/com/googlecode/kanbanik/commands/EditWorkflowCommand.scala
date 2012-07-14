package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.EditWorkflowParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.builders.WorkflowitemBuilder
import com.googlecode.kanbanik.model.WorkflowitemScala
import org.bson.types.ObjectId
import scala.util.control.Breaks.break
import scala.util.control.Breaks.breakable
import com.googlecode.kanbanik.model.KanbanikEntity
import com.mongodb.casbah.commons.MongoDBObject
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.BoardScala
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.dto.shell.SimpleParams

class EditWorkflowCommand extends ServerCommand[EditWorkflowParams, SimpleParams[WorkflowitemDto]] with KanbanikEntity {

  lazy val workflowitemBuilder = new WorkflowitemBuilder

  def execute(params: EditWorkflowParams): SimpleParams[WorkflowitemDto] = {

    val currenDto = params.getCurrent()
    val contextDto = params.getContext();
    
    var currentEntity = workflowitemBuilder.buildEntity(currenDto)

    if (contextDto != null) {
    	currentEntity = currentEntity.store(Some(WorkflowitemScala.byId(new ObjectId(contextDto.getId()))))  
    } else {
      currentEntity = currentEntity.store
    }

    new SimpleParams(workflowitemBuilder.buildDto(currentEntity))
  }


}