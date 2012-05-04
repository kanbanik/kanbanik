package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.builders.BoardBuilder

class SaveBoardCommand extends ServerCommand[SimpleParams[BoardDto], SimpleParams[BoardDto]] {
	lazy val boardBuilder = new BoardBuilder() 
  
	 def execute(params: SimpleParams[BoardDto]): SimpleParams[BoardDto] = {
	   val storedBoard = boardBuilder.buildEntity(params.getPayload()).store
	   
	   new SimpleParams(boardBuilder.buildDto(storedBoard))
	 }
}