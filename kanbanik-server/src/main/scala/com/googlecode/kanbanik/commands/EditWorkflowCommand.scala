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

class EditWorkflowCommand extends ServerCommand[EditWorkflowParams, FailableResult[SimpleParams[WorkflowitemDto]]] with HasMongoConnection {

  lazy val workflowitemBuilder = new WorkflowitemBuilder

  lazy val workflowBuilder = new WorkflowBuilder

  lazy val boardBuilder = new BoardBuilder

  def execute(params: EditWorkflowParams): FailableResult[SimpleParams[WorkflowitemDto]] = {
    val currenDto = params.getCurrent()
    val nextDto = params.getNext()
    val destContextDto = params.getDestContext()

    // hack just to test if the board still exists
    try {
      Board.byId(new ObjectId(currenDto.getParentWorkflow().getBoard().getId()))
    } catch {
      case e: IllegalArgumentException =>
        return new MidAirCollisionResult(new SimpleParams(currenDto), false, ServerMessages.entityDeletedMessage("board " + currenDto.getParentWorkflow().getBoard().getName()))
    }

    val currentBoard = boardBuilder.buildEntity(currenDto.getParentWorkflow().getBoard())

    try {
      return doExecute(currenDto, nextDto, destContextDto)
    } catch {
      case e: MidAirCollisionException =>
        return new MidAirCollisionResult(new SimpleParams(currenDto), false, ServerMessages.midAirCollisionException)
    }

  }

  private def doExecute(currenDto: WorkflowitemDto, nextDto: WorkflowitemDto, destContextDto: WorkflowDto): FailableResult[SimpleParams[WorkflowitemDto]] = {
    if (hasTasks(nextDto)) {
      return new FailableResult(new SimpleParams(currenDto), false, "The workflowitem into which you are about to drop this item already has some tasks in it which would effectively hide them. Please move this tasks first out.")
    }

    val currentEntity = workflowitemBuilder.buildEntity(currenDto, None, None)
    val nextEntity = {
      if (nextDto == null) {
        None
      } else {
        Some(workflowitemBuilder.buildEntity(nextDto, None, None))
      }
    }
    val contextEntity = workflowBuilder.buildEntity(destContextDto, None)

    val res = contextEntity.board.move(currentEntity, nextEntity, contextEntity).store
    

    new FailableResult(new SimpleParams(workflowitemBuilder.buildDto(Workflowitem.byId(currentEntity.id.get), None)))
  }

  private def hasTasks(contextDto: WorkflowitemDto): Boolean = {
    if (contextDto == null) {
      return false;
    }

    using(createConnection) { conn =>
      return coll(conn, Coll.Tasks).findOne(MongoDBObject(Task.Fields.workflowitem.toString() -> new ObjectId(contextDto.getId()))).isDefined
    }
  }

}