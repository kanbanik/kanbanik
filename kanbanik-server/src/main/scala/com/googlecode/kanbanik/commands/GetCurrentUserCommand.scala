package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.dto.UserDto
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import org.apache.shiro.SecurityUtils
import com.googlecode.kanbanik.builders.UserBuilder
import com.googlecode.kanbanik.model.User

class GetCurrentUserCommand extends ServerCommand[VoidParams, FailableResult[SimpleParams[UserDto]]] {

  lazy val userBuilder = new UserBuilder
  
  def execute(params: VoidParams): FailableResult[SimpleParams[UserDto]] = {
    val user = SecurityUtils.getSubject();
    
    if (user.isAuthenticated()) {
      val userPrincipal = user.getPrincipal().asInstanceOf[User]
    	new FailableResult(new SimpleParams(userBuilder.buildDto(userPrincipal)))
    } else {
    	new FailableResult(new SimpleParams(new UserDto), false, "No user logged in.")
    }
  }
  
}