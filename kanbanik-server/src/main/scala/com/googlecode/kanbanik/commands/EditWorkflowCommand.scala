package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.WorkflowitemBuilder
import com.googlecode.kanbanik.model.{User, Workflowitem, Board, Workflow}
import com.googlecode.kanbanik.security._
import org.bson.types.ObjectId
import com.googlecode.kanbanik.db.HasMongoConnection
import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.builders.WorkflowBuilder
import com.googlecode.kanbanik.db.HasEntityLoader
import com.googlecode.kanbanik.dtos._

class EditWorkflowCommand extends Command[EditWorkflowParams, WorkflowitemDto] with HasMongoConnection with HasEntityLoader {

  lazy val workflowitemBuilder = new WorkflowitemBuilder

  lazy val workflowBuilder = new WorkflowBuilder

  lazy val boardBuilder = new BoardBuilder

  override def execute(params: EditWorkflowParams, user: User): Either[WorkflowitemDto, ErrorDto] = {
    val currenDto = params.current
    val nextDto = params.next
    val destContextDto = params.destinationWorkflow

    // hack just to test if the board still exists
    val board = loadBoard(new ObjectId(params.board.id.getOrElse(
        return Right(ErrorDto("The board has to have the ID set"))
      )), includeTasks = false).getOrElse(
        return Right(ErrorDto(ServerMessages.entityDeletedMessage("board " + params.board.name)))
      )

    val currentBoard = board.copy(version = params.board.version)

    doExecute(currenDto, nextDto, destContextDto, currentBoard, user)

  }

  private def doExecute(currentDto: WorkflowitemDto, nextDto: Option[WorkflowitemDto], destContextDto: WorkflowDto, currentBoard: Board, user: User): Either[WorkflowitemDto, ErrorDto] = {

    if (hasTasks(destContextDto)) {
      return Right(ErrorDto("The workflowitem into which you are about to drop this item already has some tasks in it which would effectively hide them. Please move this tasks first out."))
    }

    val currentWorkflow = workflowBuilder.buildEntity(currentDto.parentWorkflow.get, Some(currentBoard))
    val currentEntityId = if (!currentDto.id.isDefined) new ObjectId else new ObjectId(currentDto.id.get)
    val currentEntityIfExists = currentWorkflow.findItem(Workflowitem().copy(id = Some(currentEntityId)))
    val currentEntity = currentEntityIfExists.getOrElse(workflowitemBuilder.buildEntity(currentDto, Some(currentWorkflow), Some(currentBoard)))
    val nextEntity = {
      if (!nextDto.isDefined) {
        None
      } else {
        Some(workflowitemBuilder.buildEntity(nextDto.get, None, None))
      }
    }
    val contextEntity = workflowBuilder.buildEntity(destContextDto, Some(currentBoard))

    val res = contextEntity.board(user).move(currentEntity, nextEntity, contextEntity).store
    val realCurrentEntity = res.workflow.findItem(currentEntity).getOrElse(throw new IllegalStateException("Was not able to find the just stored workflowitem with id: '" + currentEntity.id + "'"))
    Left(workflowitemBuilder.buildDto(realCurrentEntity, None, user))


  }

  private def hasTasks(destContextDto: WorkflowDto): Boolean = {
    val board = Board.byId(new ObjectId(destContextDto.board.id.get), includeTasks = true)
    if (!destContextDto.id.isDefined) {
      return false
    }

    val destWorkflow = Workflow().copy(id = Some(new ObjectId(destContextDto.id.get)))
    if (board.workflow == destWorkflow) {
      return false
    }

    val destParentItem = board.workflow.findParentItem(destWorkflow).getOrElse(throw new IllegalStateException("The workflow: " + destContextDto.id + " is defined on no item."))

    val tasksOnWorkflowitem = board.tasks.filter(_.workflowitemId == destParentItem.id.get)
    tasksOnWorkflowitem.size != 0
  }

  override def checkPermissions(param: EditWorkflowParams, user: User) = checkEditBoardPermissions(user, param.board.id)
}
