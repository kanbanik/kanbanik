package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.BoardsDto
import com.googlecode.kanbanik.model.BoardScala

class GetAllBoardsCommand extends ServerCommand[VoidParams, SimpleParams[BoardsDto]] {

  def execute(params: VoidParams): SimpleParams[BoardsDto] = {
    val boards = new BoardsDto
    BoardScala.all.foreach(board => {null})
    new SimpleParams(boards)
  }
}