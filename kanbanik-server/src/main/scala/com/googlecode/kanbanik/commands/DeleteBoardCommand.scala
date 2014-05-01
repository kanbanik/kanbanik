package com.googlecode.kanbanik.commands
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.db.HasMongoConnection
import com.googlecode.kanbanik.model.Project
import com.mongodb.casbah.commons.MongoDBObject
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.dtos.{ErrorDto, EmptyDto, BoardDto}

class DeleteBoardCommand extends Command[BoardDto, EmptyDto] with HasMongoConnection {

  lazy val boardBuilder = new BoardBuilder

  def execute(params: BoardDto): Either[EmptyDto, ErrorDto] = {
    val boardId = new ObjectId(params.id.getOrElse(
      return Right(ErrorDto("Board has to be defined"))
    ))

    try {
    	Board.byId(boardId, false)
    } catch {
      case e: IllegalArgumentException =>
        return Right(ErrorDto(ServerMessages.entityDeletedMessage("board")))
    }
    
    if (isOnProject(boardId)) {
      return Right(ErrorDto("There are projects on this board. Please remove them from the board first and then delete this board."))
    }

    val board = boardBuilder.buildEntity(params)
    if (board.workflow.workflowitems.size > 0) {
      return Right(ErrorDto("There are workflowitems on this board. Please delete them first and than delete this board."))
    }

    board.delete

    Left(EmptyDto())
  }
  
  def isOnProject(boardId: ObjectId): Boolean = {
    using(createConnection) { conn =>
      return coll(conn, Coll.Projects).findOne(MongoDBObject(Project.Fields.boards.toString() -> boardId)).isDefined
    }
  }
}
