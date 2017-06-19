package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.model.{Permission, User}
import net.liftweb.json._
import com.googlecode.kanbanik.dtos.ErrorDto
import com.googlecode.kanbanik.security._

abstract class Command[T: Manifest, R: Manifest] {

  implicit val formats = DefaultFormats

  def execute(parsedJson: JValue, user: User): Either[R, ErrorDto] = {
    val param: T = parsedJson.extract[T]

    val res = checkPermissions(param, user)

    if (res.isDefined) {
      Right(ErrorDto("Insufficient permissions for user '" + user.name +  "'. Missing: " + res.get.mkString("; ")))
    } else {
      execute(parsedJson.extract[T], user)
    }

  }

  def execute(dto: T, user: User): Either[R, ErrorDto] = {
    execute(dto)
  }

  def execute(dto: T): Either[R, ErrorDto] = {
    ???
  }

  def checkPermissions(param: T, user: User): Option[List[String]] = {
    None
  }

  final def filterResult(res: Any, user: User): Boolean = filter(res.asInstanceOf[R], user)

  def filter(toReturn: R, user: User): Boolean = true
}
