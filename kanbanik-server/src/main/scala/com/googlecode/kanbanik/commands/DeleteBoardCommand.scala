package com.googlecode.kanbanik.commands
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.HasMongoConnection
import com.googlecode.kanbanik.model.Project
import com.mongodb.casbah.commons.MongoDBObject
import com.googlecode.kanbanik.model.MidAirCollisionException
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.builders.BoardBuilder

class DeleteBoardCommand extends ServerCommand[SimpleParams[BoardDto], FailableResult[VoidParams]] with HasMongoConnection {

  val boardBuilder = new BoardBuilder
  
  def execute(params: SimpleParams[BoardDto]): FailableResult[VoidParams] = {
    val boardId = new ObjectId(params.getPayload().getId())

    try {
    	Board.byId(boardId)
    } catch {
      case e: IllegalArgumentException =>
        return new FailableResult(new VoidParams(), false, ServerMessages.entityDeletedMessage("board"))
    }
    
    if (isOnProject(boardId)) {
      return new FailableResult(new VoidParams, false, "There are projects on this board. Please remove them from the board first and than delete this board.")
    }

    val board = boardBuilder.buildEntity(params.getPayload())
    if (board.workflowitems.isDefined) {
      return new FailableResult(new VoidParams, false, "There are workflowitems on this board. Please delete them first and than delete this board.")
    }

    try {
    	board.delete
    } catch {
      case e: MidAirCollisionException =>
        return new FailableResult(new VoidParams(), false, ServerMessages.midAirCollisionException)
    }
    return new FailableResult(new VoidParams, true, "")

  }

  def isOnProject(boardId: ObjectId): Boolean = {
    using(createConnection) { conn =>
      return coll(conn, Coll.Projects).findOne(MongoDBObject(Project.Fields.boards.toString() -> boardId)).isDefined
    }
  }
}