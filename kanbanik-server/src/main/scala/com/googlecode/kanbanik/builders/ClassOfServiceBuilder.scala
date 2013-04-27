package com.googlecode.kanbanik.builders

import com.googlecode.kanbanik.model.ClassOfService
import com.googlecode.kanbanik.dto.ClassOfServiceDto
import com.googlecode.kanbanik.model.Board
import org.bson.types.ObjectId

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
      null)
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