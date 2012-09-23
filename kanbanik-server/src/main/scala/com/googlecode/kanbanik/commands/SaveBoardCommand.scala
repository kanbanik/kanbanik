package com.googlecode.kanbanik.commands
import org.bson.types.ObjectId

import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.MidAirCollisionException

class SaveBoardCommand extends ServerCommand[SimpleParams[BoardDto], FailableResult[SimpleParams[BoardDto]]] {
  lazy val boardBuilder = new BoardBuilder()

  def execute(params: SimpleParams[BoardDto]): FailableResult[SimpleParams[BoardDto]] = {
    var storedBoard: Board = null

    try {
      storedBoard = boardBuilder.buildEntity(params.getPayload()).store
    } catch {
      case e: MidAirCollisionException =>
        return new FailableResult(new SimpleParams(), false, "Mid Air Collistion Exception. Please refresh your browser to get the accurate data.")
    }

    new FailableResult(new SimpleParams(boardBuilder.buildDto(storedBoard)))
  }
}