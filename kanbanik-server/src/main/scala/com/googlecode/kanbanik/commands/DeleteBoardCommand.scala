package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.BoardScala
import org.bson.types.ObjectId

class DeleteBoardCommand extends ServerCommand[SimpleParams[BoardDto], FailableResult[VoidParams]] {

  def execute(params: SimpleParams[BoardDto]): FailableResult[VoidParams] = {
    val board = BoardScala.byId(new ObjectId(params.getPayload().getId()))
    if (!board.workflowitems.isDefined) {
      board.delete
      return new FailableResult(new VoidParams, true, "")
    }

    return new FailableResult(new VoidParams, false, "There are workflowitems connected to this board. Please delete them first and than delete this board.")
  }
}