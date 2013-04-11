package com.googlecode.kanbanik.builders

import com.googlecode.kanbanik.model.ClassOfService
import com.googlecode.kanbanik.dto.ClassOfServiceDto
import com.googlecode.kanbanik.model.Board
import org.bson.types.ObjectId

class ClassOfServiceBuilder extends BaseBuilder {

  lazy val boardBuilder = new BoardBuilder

  def buildDto(classOfService: ClassOfService) = {
    new ClassOfServiceDto(
      classOfService.id.get.toString,
      classOfService.name,
      classOfService.description,
      classOfService.colour,
      classOfService.isPublic,
      classOfService.version,
      boardBuilder.buildShallowDto(classOfService.board))
  }

  def buildEntity(classOfServiceDto: ClassOfServiceDto) = {
    new ClassOfService(
      determineId(classOfServiceDto),
      classOfServiceDto.getName,
      classOfServiceDto.getDescription,
      classOfServiceDto.getColour,
      classOfServiceDto.getIsPublic,
      classOfServiceDto.getVersion,
      Board().withId(new ObjectId(classOfServiceDto.getBoard.getId)))
  }

}