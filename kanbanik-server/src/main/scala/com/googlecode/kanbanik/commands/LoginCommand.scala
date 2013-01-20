package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.LoginDto
import com.googlecode.kanbanik.dto.UserDto
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.SecurityUtils
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.model.User

class LoginCommand extends ServerCommand[SimpleParams[LoginDto], FailableResult[SimpleParams[UserDto]]] {
  def execute(params: SimpleParams[LoginDto]): FailableResult[SimpleParams[UserDto]] = {
    val currentUser = SecurityUtils.getSubject()
    try {
      currentUser.login(new UsernamePasswordToken(params.getPayload().getUserName(), params.getPayload().getPassword()))
    } catch {
      case e: Exception =>
        return new FailableResult(new SimpleParams, false, "Login not successful!")
    }
    
    val principal = currentUser.getPrincipal().asInstanceOf[User]
    // TODO do this builder
    return new FailableResult(new SimpleParams(new UserDto(principal.name)))
  }
}