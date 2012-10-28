package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.builders.ProjectBuilder
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.model.MidAirCollisionException
import scala.collection.JavaConversions._
import com.googlecode.kanbanik.model.Board
import org.bson.types.ObjectId

class SaveProjectCommand extends ServerCommand[SimpleParams[ProjectDto], FailableResult[SimpleParams[ProjectDto]]] {

  private lazy val projectBuilder = new ProjectBuilder()

  def execute(params: SimpleParams[ProjectDto]): FailableResult[SimpleParams[ProjectDto]] = {

    for (board <- params.getPayload().getBoards) {
      try {
        Board.byId(new ObjectId(board.getId()))
      } catch {
        case e: IllegalArgumentException =>
          return new FailableResult(params, false, "The board '" + board.getName + "' to which this project is assigned does not exists. Possibly it has been deleted by a different user. Please refresh your browser to get the current data.")
      }
    }

    val project = projectBuilder.buildEntity(params.getPayload())
    var res: ProjectDto = null
    try {
      res = projectBuilder.buildDto(project.store)
    } catch {
      case e: MidAirCollisionException =>
        return new FailableResult(new SimpleParams(res), false, ServerMessages.midAirCollisionException)
    }
    new FailableResult(new SimpleParams(res))
  }
}