package com.googlecode.kanbanik.builders

import com.googlecode.kanbanik.dto.UserDto
import com.googlecode.kanbanik.model.User

class UserBuilder {

  def buildDto(user: User): UserDto = {
    new UserDto(user.name)
  }
  
}