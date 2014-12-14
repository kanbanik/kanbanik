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
    val BALANCED = Value(-1)
    val MIN_POSSIBLE = Value(1)

    def fromId(id: Int) = if(id == 0 || id == -1) BALANCED else MIN_POSSIBLE

  }

  object WorkflowitemType extends Enumeration {
    val HORIZONTAL = Value("H")
    val VERTICAL = Value("V")
  }

  case class BoardDto(id: Option[String],
                      version: Int,
                      name: String,
                      workflowVerticalSizing: Int,
                      workflow: Option[WorkflowDto],
                      showUserPictureEnabled: Option[Boolean],
                      fixedSizeShortDescription: Option[Boolean],
                      tasks: Option[List[TaskDto]])

  case class WorkflowDto(id: Option[String], workflowitems: Option[List[WorkflowitemDto]], board: BoardDto)

  case class GetAllBoardsWithProjectsDto(includeTasks: Option[Boolean], includeTaskDescription: Option[Boolean])

  case class BoardWithProjectsDto(board: BoardDto, projectsOnBoard: Option[ProjectsDto])

  case class WorkflowitemDto(name: String,
                             id: Option[String],
                             wipLimit: Option[Int],
                             itemType: String,
                             version: Option[Int],
                             nestedWorkflow: Option[WorkflowDto],
                             parentWorkflow: Option[WorkflowDto],
                             verticalSize: Option[Int])

  case class EditWorkflowParams(current: WorkflowitemDto, next: Option[WorkflowitemDto], destinationWorkflow: WorkflowDto, board: BoardDto)

  case class TaskDto(id: Option[String],
                     name: String,
                     description: Option[String],
                     classOfService: Option[ClassOfServiceDto],
                     ticketId: Option[String],
                     workflowitemId: String,
                     version: Int,
                     projectId: String,
                     assignee: Option[UserDto],
                     order: Option[String],
                     dueDate: Option[String],
                     boardId: String,
                     taskTags: Option[List[TaskTag]])

  case class MoveTaskDto(task: TaskDto, prevOrder: Option[String], nextOrder: Option[String])

  case class TasksDto(values: List[TaskDto])

  case class ProjectsDto(values: List[ProjectDto])

  case class ErrorDto(errorMessage: String)

  case class StatusDto(success: Boolean, reason: Option[String])

  case class ListDto[T](values: List[T])

  case class EmptyDto()

  case class GetTasksDto(includeDescription: Boolean)

  case class EventDto(source: String, payload: String)

  object TargetType extends Enumeration {
    val NOTHING = Value(0)
    val NEW_BROWSER_WINDOW = Value(1)
    val NEW_KANBANIK_WINDOW = Value(2)
  }

  case class TaskTag(id: Option[String],
                     title: String,
                     description: String,
                     pictureUrl: String,
                     onClickUrl: String,
                     onClickTarget: Int)
}
