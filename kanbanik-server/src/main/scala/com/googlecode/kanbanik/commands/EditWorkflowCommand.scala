package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.EditWorkflowParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.builders.WorkflowitemBuilder
import com.googlecode.kanbanik.model.Workflowitem
import org.bson.types.ObjectId
import scala.util.control.Breaks.break
import scala.util.control.Breaks.breakable
import com.googlecode.kanbanik.model.HasMongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.model.Task
import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.model.ResourceLockedException

class EditWorkflowCommand extends ServerCommand[EditWorkflowParams, FailableResult[SimpleParams[WorkflowitemDto]]] with HasMongoConnection {

  lazy val workflowitemBuilder = new WorkflowitemBuilder

  lazy val boardBuilder = new BoardBuilder
  
  def execute(params: EditWorkflowParams): FailableResult[SimpleParams[WorkflowitemDto]] = {

    val currenDto = params.getCurrent()
    val contextDto = params.getContext()

    val currentBoard = boardBuilder.buildEntity(currenDto.getBoard())
    
    try {
    	currentBoard.acquireLock()
    } catch {
      case e: ResourceLockedException =>
            return new FailableResult(new SimpleParams(currenDto), false, "Your workflow is not up to date. Please refresh your browser to get the current data")
    }
    
    try {
    	return doExecute(currenDto, contextDto)
    } finally {
    	currentBoard.releaseLock()
    }
    
  }
  
  private def doExecute(currenDto: WorkflowitemDto, contextDto: WorkflowitemDto): FailableResult[SimpleParams[WorkflowitemDto]] = {
    if (hasTasks(contextDto)) {
    	return new FailableResult(new SimpleParams(currenDto), false, "The workflowitem into which you are about to drop this item already has some tasks in it which would effectively hide them. Please move this tasks first out.")
    }
    
    var currentEntity = workflowitemBuilder.buildEntity(currenDto)

    if (contextDto != null) {
    	currentEntity = currentEntity.store(Some(Workflowitem.byId(new ObjectId(contextDto.getId()))))  
    } else {
      currentEntity = currentEntity.store
    }

    
    new FailableResult(new SimpleParams(workflowitemBuilder.buildDto(currentEntity)))
  }

  private def hasTasks(contextDto : WorkflowitemDto) : Boolean = {
    if (contextDto == null) {
      return false;
    }
   
    using(createConnection) { conn =>
      return coll(conn, Coll.Tasks).findOne(MongoDBObject(Task.Fields.workflowitem.toString() -> new ObjectId(contextDto.getId()))).isDefined
    }
  }

}