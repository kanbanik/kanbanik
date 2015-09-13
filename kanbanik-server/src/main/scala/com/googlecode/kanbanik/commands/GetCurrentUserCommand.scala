package com.googlecode.kanbanik.commands


import org.apache.shiro.SecurityUtils
import com.googlecode.kanbanik.builders.UserBuilder
import com.googlecode.kanbanik.model.User
import org.apache.shiro.subject.Subject
import com.googlecode.kanbanik.dtos.{SessionDto, ErrorDto, UserDto, EmptyDto}

class GetCurrentUserCommand extends Command[SessionDto, UserDto] {

  override def execute(params: SessionDto, user: User): Either[UserDto, ErrorDto] = {
    val sessionId = params.sessionId

    val user = if (sessionId.isDefined) {
      new Subject.Builder().sessionId(sessionId.get).buildSubject
    } else {
      SecurityUtils.getSubject
    }

    if (user.isAuthenticated) {
      val userPrincipal = user.getPrincipal.asInstanceOf[User]
      // refresh from DB
    	Left(UserBuilder.buildDto(User.byId(userPrincipal.name), sessionId.getOrElse("")))
    } else {
    	Left(UserBuilder.buildDto(User.unlogged, ""))
    }
  }
  
}