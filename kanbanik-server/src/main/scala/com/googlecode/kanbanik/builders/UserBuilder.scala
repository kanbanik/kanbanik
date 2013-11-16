package com.googlecode.kanbanik.builders

import com.googlecode.kanbanik.dto.UserDto
import com.googlecode.kanbanik.dtos.{UserDto => NewUserDto}
import com.googlecode.kanbanik.model.User

class UserBuilder {

  def buildDto(user: User) = new UserDto(user.name, user.realName, user.pictureUrl, user.version)

  def buildDto2(user: User) = new NewUserDto(user.name, user.realName, user.pictureUrl, user.version)
  
  def buildEntity(userDto: UserDto) = User(userDto.getUserName())
}