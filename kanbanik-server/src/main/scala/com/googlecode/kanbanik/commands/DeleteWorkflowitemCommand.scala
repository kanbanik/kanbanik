package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.security._
import org.bson.types.ObjectId
import com.googlecode.kanbanik.builders.{BoardBuilder, TaskBuilder, WorkflowitemBuilder}
import com.googlecode.kanbanik.db.HasMongoConnection
import com.googlecode.kanbanik.model.{User, Workflowitem}
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.db.HasEntityLoader
import com.googlecode.kanbanik.dtos._

class DeleteWorkflowitemCommand extends Command[DeleteWorkflowitemDto, EmptyDto] with HasMongoConnection with HasEntityLoader {

  lazy val workflowitemBuilder = new WorkflowitemBuilder

  lazy val boardBuilder = new BoardBuilder

  private lazy val taskBuilder = new TaskBuilder

  override def execute(params: DeleteWorkflowitemDto, user: User): Either[EmptyDto, ErrorDto] = {

    val theId = new ObjectId(params.workflowitem.id.getOrElse(
    return Right(ErrorDto("The ID has to be defined"))
    ))

    val board = loadBoard(new ObjectId(params.workflowitem.parentWorkflow.get.board.id.getOrElse(
      return Right(ErrorDto("The ID of the Board has to be set"))
    )), includeTasks = true).getOrElse(
      return Right(ErrorDto(ServerMessages.entityDeletedMessage("board " + params.workflowitem.parentWorkflow.get.board.name)))
    )

    val item = Workflowitem().copy(id = Some(theId))

    try {
      board.workflow.containsItem(item)
    } catch {
      case e: IllegalArgumentException =>
        return Right(ErrorDto(ServerMessages.entityDeletedMessage("workflowitem")))
    }

    val foundItem = board.workflow.findItem(item)
    if (foundItem.isDefined && foundItem.get.nestedWorkflow.workflowitems.size > 0) {
      return Right(ErrorDto("This workflowitem can not be deleted, because it has a nested workflow. Please delete it first"))
    }

    val tasksOnWorkflowitem = board.tasks.filter(_.workflowitemId == theId)
    if (tasksOnWorkflowitem.size != 0) {
      if (params.includingTasks.getOrElse(false)) {
        val tasksToDelete = TasksDto(tasksOnWorkflowitem.map(taskBuilder.buildDto(_)))
        val deleteTasksCommand = new DeleteTasksCommand()
        val permissionCheckRes = deleteTasksCommand.checkPermissions(tasksToDelete, user)
        if (permissionCheckRes.isDefined) {
          return Right(ErrorDto(
            "Attempt to delete tasks on workflowitem but the user does not have permissions to delete this tasks: " +
              permissionCheckRes.get.mkString(", ")))
        } else {
          deleteTasksCommand.execute(tasksToDelete, user) match {
            case Left(x) =>
            // nothing to do
            case Right(x) =>
              return Right(x)
          }
        }
      } else {
        val ticketIds = tasksOnWorkflowitem.map(_.ticketId).mkString(", ")
        return Right(ErrorDto("This workflowitem can not be deleted, because there are tasks associated with this workflowitem. Tasks: [" + ticketIds + "]"))
      }
    }

    board.copy(workflow = board.workflow.removeItem(foundItem.getOrElse(
      return Right(ErrorDto("This workflowitem has been deleted by a different user. Please refresh your browser to get the current data"))
    ))).store

    Left(EmptyDto())
  }

  override def checkPermissions(param: DeleteWorkflowitemDto, user: User) = {
    if (param.workflowitem.parentWorkflow.isDefined) {
      checkEditBoardPermissions(user, param.workflowitem.parentWorkflow.get.board.id)
    } else {
      None
    }
  }
}
