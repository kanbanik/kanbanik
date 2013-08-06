package com.googlecode.kanbanik.builders

import org.bson.types.ObjectId

import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.dto.ClassOfServiceDto
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.ClassOfService

class ClassOfServiceBuilder extends BaseBuilder {

  lazy val boardBuilder = new BoardBuilder

  def buildDto(classOfService: ClassOfService) = {
    new ClassOfServiceDto(
      classOfService.id.get.toString,
      classOfService.name,
      classOfService.description,
      classOfService.colour,
      classOfService.version
    )
  }

  def buildEntity(classOfServiceDto: ClassOfServiceDto) = {
    new ClassOfService(
      determineId(classOfServiceDto),
      classOfServiceDto.getName,
      classOfServiceDto.getDescription,
      classOfServiceDto.getColour,
      classOfServiceDto.getVersion
    )
  }

}