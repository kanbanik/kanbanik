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
import com.googlecode.kanbanik.dto.shell.FailableResult

class EditWorkflowCommand extends ServerCommand[EditWorkflowParams, FailableResult[SimpleParams[WorkflowitemDto]]] with KanbanikEntity {

  lazy val workflowitemBuilder = new WorkflowitemBuilder

  def execute(params: EditWorkflowParams): FailableResult[SimpleParams[WorkflowitemDto]] = {

    val currenDto = params.getCurrent()
    val contextDto = params.getContext();

    if (hasTasks(contextDto)) {
    	return new FailableResult(new SimpleParams(currenDto), false, "The workflowitem into which you are about to drop this item already has some tasks in it which would effectively hide them. Please move this tasks first out.")
    }
    
    var currentEntity = workflowitemBuilder.buildEntity(currenDto)

    if (contextDto != null) {
    	currentEntity = currentEntity.store(Some(WorkflowitemScala.byId(new ObjectId(contextDto.getId()))))  
    } else {
      currentEntity = currentEntity.store
    }

    new FailableResult(new SimpleParams(workflowitemBuilder.buildDto(currentEntity)), true, "")
  }

  private def hasTasks(contextDto : WorkflowitemDto) : Boolean = {
    if (contextDto == null) {
      return false;
    }
   
    using(createConnection) { conn =>
      return coll(conn, Coll.Tasks).findOne(MongoDBObject("workflowitem" -> new ObjectId(contextDto.getId()))).isDefined
    }
  }

}