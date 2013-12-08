package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.UserBuilder
import com.googlecode.kanbanik.model.User
import com.googlecode.kanbanik.dtos.{ErrorDto, UserDto, ManipulateUserDto}

class EditUserCommand extends Command[ManipulateUserDto, UserDto] with CredentialsUtils {

  lazy val userBuilder = new UserBuilder

  def execute(params: ManipulateUserDto): Either[UserDto, ErrorDto] = {

    if (!isAuthenticated(params.userName, params.password)) {
      Right(ErrorDto("The credentials provided are not correct!"))
    } else {
      val user = User.byId(params.userName)
      val (resPassword, resSalt): (String, String) = hashPassword(params.newPassword)

      val newUser = user.copy(
        password = resPassword,
        salt = resSalt,
        realName = params.realName,
        version = params.version,
        pictureUrl = params.pictureUrl
      ).store

      new Left(userBuilder.buildDto2(newUser, params.sessionId))
    }
  }
}