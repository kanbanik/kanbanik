package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.EditWorkflowParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.builders.WorkflowitemBuilder
import com.googlecode.kanbanik.model.Workflowitem
import org.bson.types.ObjectId
import scala.util.control.Breaks.break
import scala.util.control.Breaks.breakable
import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.MidAirCollisionResult
import com.googlecode.kanbanik.model.Task
import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.exceptions.ResourceLockedException
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.dto.WorkflowDto
import com.googlecode.kanbanik.builders.WorkflowBuilder
import com.googlecode.kanbanik.model.Workflow
import com.googlecode.kanbanik.db.HasEntityLoader

class EditWorkflowCommand extends ServerCommand[EditWorkflowParams, FailableResult[SimpleParams[WorkflowitemDto]]] with HasMongoConnection with HasEntityLoader{

  lazy val workflowitemBuilder = new WorkflowitemBuilder

  lazy val workflowBuilder = new WorkflowBuilder

  lazy val boardBuilder = new BoardBuilder

  def execute(params: EditWorkflowParams): FailableResult[SimpleParams[WorkflowitemDto]] = {
    val currenDto = params.getCurrent()
    val nextDto = params.getNext()
    val destContextDto = params.getDestContext()

    // hack just to test if the board still exists
    val board = loadBoard(new ObjectId(currenDto.getParentWorkflow().getBoard().getId()), false).getOrElse(
    		return new MidAirCollisionResult(new SimpleParams(currenDto), false, ServerMessages.entityDeletedMessage("board " + currenDto.getParentWorkflow().getBoard().getName()))    
    )

    val currentBoard = boardBuilder.buildEntity(currenDto.getParentWorkflow().getBoard())

    try {
      return doExecute(currenDto, nextDto, destContextDto, currentBoard)
    } catch {
      case e: MidAirCollisionException =>
        return new MidAirCollisionResult(new SimpleParams(currenDto), false, ServerMessages.midAirCollisionException)
    }

  }

  private def doExecute(currentDto: WorkflowitemDto, nextDto: WorkflowitemDto, destContextDto: WorkflowDto, currentBoard: Board): FailableResult[SimpleParams[WorkflowitemDto]] = {
   
    if (hasTasks(destContextDto)) {
      return new FailableResult(new SimpleParams(currentDto), false, "The workflowitem into which you are about to drop this item already has some tasks in it which would effectively hide them. Please move this tasks first out.")
    }

    val currentWorkflow = workflowBuilder.buildEntity(currentDto.getParentWorkflow(), Some(currentBoard))
    val currentEntityId = if (currentDto.getId() == null) new ObjectId else new ObjectId(currentDto.getId()) 
    val currentEntityIfExists = currentWorkflow.findItem(Workflowitem().withId(currentEntityId))
    val currentEntity = currentEntityIfExists.getOrElse(workflowitemBuilder.buildEntity(currentDto, Some(currentWorkflow), Some(currentBoard)))
    val nextEntity = {
      if (nextDto == null) {
        None
      } else {
        Some(workflowitemBuilder.buildEntity(nextDto, None, None))
      }
    }
    val contextEntity = workflowBuilder.buildEntity(destContextDto, Some(currentBoard))

    try {
    	val res = contextEntity.board.move(currentEntity, nextEntity, contextEntity).store
    	val realCurrentEntity = res.workflow.findItem(currentEntity).getOrElse(throw new IllegalStateException("Was not able to find the just stored workflowitem with id: '" + currentEntity.id + "'"))
    	return new FailableResult(new SimpleParams(workflowitemBuilder.buildDto(realCurrentEntity, None)))
    } catch {
      case e: MidAirCollisionException =>
        return new MidAirCollisionResult(new SimpleParams(currentDto), false, ServerMessages.midAirCollisionException)
    }
    
    return new MidAirCollisionResult(new SimpleParams(currentDto), false, "Something went wrong - please see logs for more details")
  }

  private def hasTasks(destContextDto: WorkflowDto): Boolean = {
    val board = Board.byId(new ObjectId(destContextDto.getBoard().getId()), true)
    if (destContextDto.getId() == null) {
      return false
    }
    
    val destWorkflow = Workflow().withId(new ObjectId(destContextDto.getId()))
    if (board.workflow == destWorkflow) {
      return false
    }
    
    val destPasrentItem = board.workflow.findParentItem(destWorkflow).getOrElse(throw new IllegalStateException("The workflow: " + destContextDto.getId() + " is defined on no item."))

    val tasksOnWorkflowitem = board.tasks.filter(_.workflowitem == destPasrentItem)
    tasksOnWorkflowitem.size != 0
  }

}
