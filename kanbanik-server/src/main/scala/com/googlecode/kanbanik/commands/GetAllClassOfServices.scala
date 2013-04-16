package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.dto.ClassOfServiceDto
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.dto.ListDto
import com.googlecode.kanbanik.builders.ClassOfServiceBuilder
import com.googlecode.kanbanik.model.ClassOfService
import com.googlecode.kanbanik.model.Board
import org.bson.types.ObjectId
import java.util.ArrayList

class GetAllClassOfServices extends ServerCommand[SimpleParams[BoardDto], SimpleParams[ListDto[ClassOfServiceDto]]] {

  val builder = new ClassOfServiceBuilder

  def execute(params: SimpleParams[BoardDto]): SimpleParams[ListDto[ClassOfServiceDto]] = {
    val resAsEntities = getAll(params.getPayload())
    val dtos = resAsEntities.map(builder.buildDto(_))

    new SimpleParams(new ListDto(toJavaList(dtos)))
  }

  def toJavaList(dtos: List[ClassOfServiceDto]): java.util.List[ClassOfServiceDto] = {
    val res = new ArrayList[ClassOfServiceDto]
    dtos.foreach(res.add(_))
    res
  }
  
  def getAll(boardDto: BoardDto) = {
    if (boardDto == null) {
      ClassOfService.allShared
    } else {
      ClassOfService.allForBoard(Board().withId(new ObjectId(boardDto.getId)))
    }
  }
}