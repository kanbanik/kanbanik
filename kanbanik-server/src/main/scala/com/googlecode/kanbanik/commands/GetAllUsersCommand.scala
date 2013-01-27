package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.ListDto
import com.googlecode.kanbanik.dto.UserDto
import com.googlecode.kanbanik.model.User
import com.googlecode.kanbanik.builders.UserBuilder

class GetAllUsersCommand extends ServerCommand[VoidParams, SimpleParams[ListDto[UserDto]]] with CredentialsUtils {

  lazy val userBuilder = new UserBuilder
  
  def execute(params: VoidParams): SimpleParams[ListDto[UserDto]] = {
    val dtos = User.all.map(userBuilder.buildDto(_))
    val res = new ListDto[UserDto]
    for(dto <- dtos) res.addItem(dto) 
    new SimpleParams(res)
  }
  
}