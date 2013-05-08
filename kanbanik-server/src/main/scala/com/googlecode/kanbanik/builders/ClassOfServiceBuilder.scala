package com.googlecode.kanbanik.builders

import org.bson.types.ObjectId

import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.dto.ClassOfServiceDto
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.ClassOfService

class ClassOfServiceBuilder extends BaseBuilder {

  lazy val boardBuilder = new BoardBuilder

  def buildShallowDto(classOfService: ClassOfService) = {
    new ClassOfServiceDto(
      classOfService.id.get.toString,
      classOfService.name,
      classOfService.description,
      classOfService.colour,
      classOfService.isPublic,
      classOfService.version,
      shallowBoardFrom(classOfService))
  }

  def shallowBoardFrom(classOfService: ClassOfService) = {
    if (!classOfService.board.isDefined) {
      null
    }

    val res = new BoardDto
    res.setId(classOfService.board.get.id.get.toString)
    res
  }

  def buildDto(classOfService: ClassOfService) = {
    val res = buildShallowDto(classOfService)
    res.setBoard({
      if (classOfService.board.isDefined) {
        boardBuilder.buildShallowDto(classOfService.board.get)
      } else {
        null
      }
    })
    res
  }

  def buildEntity(classOfServiceDto: ClassOfServiceDto) = {
    new ClassOfService(
      determineId(classOfServiceDto),
      classOfServiceDto.getName,
      classOfServiceDto.getDescription,
      classOfServiceDto.getColour,
      classOfServiceDto.getIsPublic,
      classOfServiceDto.getVersion,
      {
        if (classOfServiceDto.getBoard != null && !classOfServiceDto.getIsPublic()) {
          Some(Board().withId(new ObjectId(classOfServiceDto.getBoard.getId)))
        } else {
          None
        }
      })
  }

}