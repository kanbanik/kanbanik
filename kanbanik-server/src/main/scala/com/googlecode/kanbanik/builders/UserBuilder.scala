package com.googlecode.kanbanik.builders

import com.googlecode.kanbanik.dtos.UserDto
import com.googlecode.kanbanik.model.User

object UserBuilder {

  def buildDto(user: User, sessionId: String) = new UserDto(user.name,
    user.realName,
    user.pictureUrl,
    sessionId,
    user.version,
    Some(user.permissions.map(PermissionsBuilder.buildDto(_))),
    Some(user.unloggedFakeUser)
  )

  def buildEntity(userDto: UserDto) = User(userDto.userName)
}