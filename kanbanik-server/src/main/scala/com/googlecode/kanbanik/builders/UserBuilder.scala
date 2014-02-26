package com.googlecode.kanbanik.builders

import com.googlecode.kanbanik.dtos.{UserDto => NewUserDto}
import com.googlecode.kanbanik.model.User

class UserBuilder {

  def buildDto2(user: User, sessionId: String) = new NewUserDto(user.name, user.realName, user.pictureUrl, sessionId, user.version)

  def buildEntity2(userDto: NewUserDto) = User(userDto.userName)
}