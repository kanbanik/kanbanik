package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.model.User
import net.liftweb.json._
import com.googlecode.kanbanik.dtos.ErrorDto
import com.googlecode.kanbanik.security._

abstract class Command[T: Manifest, R] {

  implicit val formats = DefaultFormats

  def execute(parsedJson: JValue, user: User): Either[R, ErrorDto] = {
    val param: T = parsedJson.extract[T]
    val res = user.checkPermissions(this, param)
    if (res.isDefined) {
      Right(ErrorDto("Insufficient permissions. Missing: " + res.get.mkString("; ")))
    } else {
      execute(parsedJson.extract[T])
    }

  }

  def execute(dto: T, user: User): Either[R, ErrorDto] = {
    execute(dto)
  }

  def execute(dto: T): Either[R, ErrorDto]
}
