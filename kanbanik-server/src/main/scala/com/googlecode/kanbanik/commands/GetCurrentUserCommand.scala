package com.googlecode.kanbanik.commands


import org.apache.shiro.SecurityUtils
import com.googlecode.kanbanik.builders.UserBuilder
import com.googlecode.kanbanik.model.User
import org.apache.shiro.subject.Subject
import com.googlecode.kanbanik.dtos.{SessionDto, ErrorDto, UserDto, EmptyDto}

class GetCurrentUserCommand extends Command[SessionDto, UserDto] {

  def execute(params: SessionDto): Either[UserDto, ErrorDto] = {
    val sessionId = params.sessionId

    val user = if (sessionId != null && sessionId != "") {
      new Subject.Builder().sessionId(sessionId).buildSubject
    } else {
      SecurityUtils.getSubject
    }

    if (user.isAuthenticated) {
      val userPrincipal = user.getPrincipal.asInstanceOf[User]
    	Left(UserBuilder.buildDto(userPrincipal, sessionId))
    } else {
    	Right(ErrorDto("No user logged in."))
    }
  }
  
}