package com.googlecode.kanbanik

package object dtos {

  case class LoginDto(commandName: String, userName: String, password: String)

  case class SessionDto(sessionId: Option[String])

  case class UserDto(userName: String, realName: String, pictureUrl: String, sessionId: String, version: Int, permissions: Option[List[PermissionDto]], unlogged: Option[Boolean])

  case class ManipulateUserDto(userName: String, realName: String, pictureUrl: String, sessionId: Option[String], version: Int, password: String, newPassword: String, permissions: Option[List[PermissionDto]])

  case class PermissionDto(permissionType: Int, args: List[String])

  object PermissionType extends Enumeration {

    val ReadUser = Value(5)
    // change user basic data (name, password, picture etc) - e.g. everything but permissions
    val EditUserData = Value(1)
    // allows to edit only the permissions
    val EditUserPermissions = Value(7)
    val CreateUser = Value(8)
    val DeleteUser = Value(9)

    val EditBoard = Value(21)
    val ReadBoard = Value(3)
    val CreateBoard = Value(22)
    val DeleteBoard = Value(23)

    val ReadProject = Value(4)
    val EditProject = Value(24)
    val CreateProject = Value(25)
    val DeleteProject = Value(26)

    val ReadClassOfService = Value(6)
    val CreateClassOfService = Value(18)
    val EditClassOfService = Value(19)
    val DeleteClassOfService = Value(20)

    val MoveTask_p = Value(10)
    val CreateTask_p = Value(11)
    val EditTask_p = Value(12)
    val DeleteTask_p = Value(13)

    val MoveTask_b = Value(14)
    val CreateTask_b = Value(15)
    val EditTask_b = Value(16)
    val DeleteTask_b = Value(17)
  }

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

  case class FilterDto(bid: Option[String], pid: Option[String], bname: Option[String], pname: Option[String])

  case class GetAllBoardsWithProjectsDto(includeTasks: Option[Boolean], includeTaskDescription: Option[Boolean], filters: Option[List[FilterDto]])

  case class BoardWithProjectsDto(board: BoardDto, projectsOnBoard: Option[ProjectsDto])

  case class DeleteWorkflowitemDto(workflowitem: WorkflowitemDto, includingTasks: Option[Boolean])

  case class WorkflowitemDto(name: String,
                             id: Option[String],
                             wipLimit: Option[Int],
                             itemType: String,
                             version: Option[Int],
                             nestedWorkflow: Option[WorkflowDto],
                             parentWorkflow: Option[WorkflowDto],
                             verticalSize: Option[Int],
                             boardId: Option[String])

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
                     name: String,
                     description: Option[String],
                     pictureUrl: Option[String],
                     onClickUrl: Option[String],
                     onClickTarget: Option[Int],
                     colour: Option[String])
}
