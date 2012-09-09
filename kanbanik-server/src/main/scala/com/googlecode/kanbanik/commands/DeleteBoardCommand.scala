package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.Board
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.HasMongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import com.googlecode.kanbanik.model.Project

class DeleteBoardCommand extends ServerCommand[SimpleParams[BoardDto], FailableResult[VoidParams]] with HasMongoConnection {

  def execute(params: SimpleParams[BoardDto]): FailableResult[VoidParams] = {

    val boardId = new ObjectId(params.getPayload().getId())
    if (isOnProject(boardId)) {
      return new FailableResult(new VoidParams, false, "There are projects on this board. Please remove them from the board first and than delete this board.")
    }

    val board = Board.byId(boardId)
    if (board.workflowitems.isDefined) {
      return new FailableResult(new VoidParams, false, "There are workflowitems on this board. Please delete them first and than delete this board.")
    }

    board.delete
    return new FailableResult(new VoidParams, true, "")

  }

  def isOnProject(boardId: ObjectId): Boolean = {
    using(createConnection) { conn =>
      return coll(conn, Coll.Projects).findOne(MongoDBObject(Project.Fields.boards.toString() -> boardId)).isDefined
    }
  }
}