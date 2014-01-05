package com.googlecode.kanbanik

package object dtos {

  case class LoginDto(commandName: String, userName: String, password: String)

  case class SessionDto(sessionId: String)

  case class UserDto(userName: String, realName: String, pictureUrl: String, sessionId: String, version: Int)

  case class ManipulateUserDto(userName: String, realName: String, pictureUrl: String, sessionId: String, version: Int, password: String, newPassword: String)

  case class ClassOfServiceDto(id: Option[String], name: String, description: String, colour: String, version: Int, sessionId: Option[String])

  case class ErrorDto(errorMessage: String)

  case class StatusDto(success: Boolean, reason: Option[String])

  case class ListDto[T](result: List[T])

  case class EmptyDto()

}
