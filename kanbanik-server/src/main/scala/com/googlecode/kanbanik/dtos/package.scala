package com.googlecode.kanbanik

package object dtos {

  case class LoginDto(commandName: String, userName: String, password: String)

  case class SessionDto(sessionId: String)

  case class UserDto(userName: String, realName: String, pictureUrl: String, sessionId: String, version: Int)

  case class ManipulateUserDto(userName: String, realName: String, pictureUrl: String, sessionId: String, version: Int, password: String, newPassword: String)

  case class ClassOfServiceDto(id: Option[String], name: String, description: String, colour: String, version: Int, sessionId: Option[String])

  case class ProjectDto(id: Option[String], name: Option[String], boardIds: Option[List[String]], version: Int)

  case class ProjectWithBoardDto(project: ProjectDto, boardId: String)

  object WorkfloVerticalSizing extends Enumeration {
    val BALANCED = Value(0)
    val MIN_POSSIBLE = Value(1)
  }

  object WorkflowitemType extends Enumeration {
    val HORIZONTAL = Value("H")
    val VERTICAL = Value("V")
  }

  case class BoardDto(id: String, version: Int, name: String, workflowVerticalSizing: Int, workflow: Option[WorkflowDto], showUserPictureEnabled: Boolean, tasks: Option[List[TaskDto]])

  case class WorkflowDto(id: String, workflowitems: Option[List[WorkflowitemDto]], board: BoardDto)

  case class WorkflowitemDto(name: String, id: String, wipLimit: Option[Int], itemType: String, version: Int, nestedWorkflow: Option[WorkflowDto], parentWorkflow: Option[WorkflowDto], verticalSize: Option[Int])

  case class TaskDto(id: String, name: String, description: String, classOfService: ClassOfServiceDto, ticketId: String, workflowitem: WorkflowitemDto, version: Int, project: ProjectDto, assignee: Option[UserDto], dueDate: Option[String])

  case class ErrorDto(errorMessage: String)

  case class StatusDto(success: Boolean, reason: Option[String])

  case class ListDto[T](result: List[T])

  case class EmptyDto()

}
