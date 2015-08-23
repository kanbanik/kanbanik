package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.model.{Permission, User}
import net.liftweb.json._
import com.googlecode.kanbanik.dtos.ErrorDto
import com.googlecode.kanbanik.security._

abstract class Command[T: Manifest, R] {

  implicit val formats = DefaultFormats

  def execute(parsedJson: JValue, user: User): Either[R, ErrorDto] = {
    val param: T = parsedJson.extract[T]

    val res = checkPermissions[T](param, user)

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

  def checkPermissions[T](param: T, user: User): Option[List[String]] = {
    // temporarily bypass permission check for all commands
    // it will stay this way only until the development of this part will be done - never release this way
    None
  }
}
