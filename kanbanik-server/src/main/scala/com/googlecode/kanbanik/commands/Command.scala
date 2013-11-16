package com.googlecode.kanbanik.commands

import net.liftweb.json._
import com.googlecode.kanbanik.dtos.ErrorDto

abstract class Command[T: Manifest, R] {

  implicit val formats = DefaultFormats

  def execute(parsedJson: JValue): Either[R, ErrorDto] = {
    execute(parsedJson.extract[T])
  }

  def execute(dto: T): Either[R, ErrorDto]
}
