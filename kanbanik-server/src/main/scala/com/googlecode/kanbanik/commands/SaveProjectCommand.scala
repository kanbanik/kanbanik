package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.builders.ProjectBuilder
import com.googlecode.kanbanik.security._
import scala.collection.JavaConversions._
import com.googlecode.kanbanik.model.{User, Board}
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dtos.{PermissionType, ClassOfServiceDto, ErrorDto, ProjectDto}

class SaveProjectCommand extends Command[ProjectDto, ProjectDto] {

  private lazy val projectBuilder = new ProjectBuilder()

  override def execute(params: ProjectDto, user: User): Either[ProjectDto, ErrorDto] = {

    if (params.boardIds.isDefined) {
      for (board <- params.boardIds.get) {
        try {
          Board.byId(new ObjectId(board), includeTasks = false)
        } catch {
          case e: IllegalArgumentException =>
            Right(ErrorDto("The board '" + board + "' to which this project is assigned does not exists. Possibly it has been deleted by a different user. Please refresh your browser to get the current data."))
        }
      }
    }

    val project = projectBuilder.buildEntity(params)

    val entity = project.store(user)

    addMePermissions(user, params.id,
      entity.id.get.toString,
      PermissionType.CreateTask_p,
      PermissionType.MoveTask_p,
      PermissionType.EditTask_p,
      PermissionType.DeleteTask_p,
      PermissionType.ReadProject,
      PermissionType.EditProject,
      PermissionType.DeleteProject)

    Left(projectBuilder.buildDto(entity))
  }

  override def checkPermissions(param: ProjectDto, user: User): Option[List[String]] =
    checkSavePermissions(user, param.id, PermissionType.CreateProject, PermissionType.EditProject)

  override def filter(toReturn: ProjectDto, user: User): Boolean =
    canRead(user, PermissionType.ReadProject, toReturn.id.getOrElse(""))

}