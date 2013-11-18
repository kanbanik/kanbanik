package com.googlecode.kanbanik

package object dtos {

  case class LoginDto(commandName: String, userName: String, password: String)

  case class UserDto(userName: String, realName: String, pictureUrl: String, sessionId: String, version: Int)

  case class ErrorDto(errorMessage: String)

}
