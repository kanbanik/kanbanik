package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.builders.ProjectBuilder
import scala.collection.JavaConversions._
import com.googlecode.kanbanik.model.Board
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dtos.{ErrorDto, ProjectDto}

class SaveProjectCommand extends Command[ProjectDto, ProjectDto] {

  private lazy val projectBuilder = new ProjectBuilder()

  def execute(params: ProjectDto): Either[ProjectDto, ErrorDto] = {

    if (params.boardIds.isDefined) {
      for (board <- params.boardIds.get) {
        try {
          Board.byId(new ObjectId(board), false)
        } catch {
          case e: IllegalArgumentException =>
            Right(ErrorDto("The board '" + board + "' to which this project is assigned does not exists. Possibly it has been deleted by a different user. Please refresh your browser to get the current data."))
        }
      }
    }

    val project = projectBuilder.buildEntity(params)
    Left(projectBuilder.buildDto(project.store))
  }
  
}