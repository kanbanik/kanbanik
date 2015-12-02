package com.googlecode.kanbanik.builders

import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.{User, Board, Workflowitem, Workflow}
import com.googlecode.kanbanik.dtos.{WorkflowitemDto, WorkflowDto}


class WorkflowitemBuilder extends BaseBuilder {

  def buildShallowEntity(dto: WorkflowitemDto, parentWorkflow: Option[Workflow], board: Option[Board]): Workflowitem = {
    new Workflowitem(
    {
      if (dto.id == null || !dto.id.isDefined) {
        Some(new ObjectId)
      } else {
        Some(new ObjectId(dto.id.get))
      }
    },
    dto.name,
    dto.wipLimit.getOrElse(0),
    dto.verticalSize.getOrElse(-1),
    dto.itemType,
    dto.version.getOrElse(0),
    // don't calculate it if not needed
    Workflow(),
    parentWorkflow
    )
  }

  def buildEntity(dto: WorkflowitemDto, parentWorkflow: Option[Workflow], board: Option[Board]): Workflowitem = {
    val shallow = buildShallowEntity(dto, parentWorkflow, board)
    if (dto.nestedWorkflow.isDefined) {
      shallow.copy(nestedWorkflow = workflowBuilder.buildEntity(dto.nestedWorkflow.get, board))
    } else {
      shallow
    }

  }

  def buildShallowDto(workflowitem: Workflowitem, parentWorkflow: Option[WorkflowDto], user: User): WorkflowitemDto =
    buildShallowDto(workflowitem, parentWorkflow, user, None)


  def buildShallowDto(workflowitem: Workflowitem, parentWorkflow: Option[WorkflowDto], user: User, boardId: Option[String]): WorkflowitemDto = {
    WorkflowitemDto(
      workflowitem.name,
      Some(workflowitem.id.get.toString),
      Some(workflowitem.wipLimit),
      workflowitem.itemType,
      Some(workflowitem.version),
      None,
      Some(parentWorkflow.getOrElse(workflowBuilder.buildShallowDto(workflowitem.parentWorkflow(user), parentBoard(parentWorkflow), user))),
      Some(workflowitem.verticalSize),
      boardId
    )
  }

  def buildDto(workflowitem: Workflowitem, parentWorkflow: Option[WorkflowDto], user: User): WorkflowitemDto =
    buildDto(workflowitem, parentWorkflow, user, None)

  def buildDto(workflowitem: Workflowitem, parentWorkflow: Option[WorkflowDto], user: User, boardId: Option[String]): WorkflowitemDto = {
    val dto = buildShallowDto(workflowitem, parentWorkflow, user, boardId)
    dto.copy(
      nestedWorkflow = Some(workflowBuilder.buildDto(workflowitem.nestedWorkflow, parentBoard(parentWorkflow), user)),
      parentWorkflow = Some(parentWorkflow.getOrElse(workflowBuilder.buildDto(workflowitem.parentWorkflow(user), parentBoard(parentWorkflow), user)))
    )
  }

  def parentBoard(parentWorkflow: Option[WorkflowDto]) = {
    if (!parentWorkflow.isDefined) {
      None
    } else {
      Some(parentWorkflow.get.board)
    }
  }

  def workflowBuilder = new WorkflowBuilder
}
  
