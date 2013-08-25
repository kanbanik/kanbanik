package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.UserBuilder
import com.googlecode.kanbanik.dto.ManipulateUserDto
import com.googlecode.kanbanik.dto.UserDto
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.model.User
import com.googlecode.kanbanik.db.HasMongoConnection

class CreateUserCommand extends ServerCommand[SimpleParams[ManipulateUserDto], FailableResult[SimpleParams[UserDto]]] with CredentialsUtils with HasMongoConnection {

  lazy val userBuilder = new UserBuilder
  
  def execute(params: SimpleParams[ManipulateUserDto]): FailableResult[SimpleParams[UserDto]] = {

    if (User(params.getPayload().getUserName()).exists) {
      return new FailableResult(new SimpleParams(new UserDto), false, "The user with this name already exists!")
    }
    
    val name = params.getPayload().getUserName()
    
    if (name == null || name == "") {
      return new FailableResult(new SimpleParams(new UserDto), false, "The user needs to have the name set!")
    }
    
    val hashedPass: (String, String) = hashPassword(params.getPayload().getPassword())
    
    val user = new User(
    		params.getPayload().getUserName(),
    		hashedPass._1,
    		params.getPayload().getRealName(),
    		params.getPayload().getPictureUrl(),
    		hashedPass._2,
    		1
    ).store
    
    new FailableResult(new SimpleParams(userBuilder.buildDto(user)))
  }

}