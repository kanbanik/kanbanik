package com.googlecode.kanbanik.commands

import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.SecurityUtils
import com.googlecode.kanbanik.model.User
import com.googlecode.kanbanik.builders.UserBuilder
import com.googlecode.kanbanik.dtos.{ErrorDto, UserDto, LoginDto}

class LoginCommand extends Command[LoginDto, UserDto] {
  
  lazy val userBuilder = new UserBuilder
  
  // 8h - one working day
  lazy val timeout = 28800000;
  
  def execute(params: LoginDto): Either[UserDto, ErrorDto] = {
    val currentUser = SecurityUtils.getSubject()
    try {
      currentUser.login(new UsernamePasswordToken(params.userName, params.password))
      SecurityUtils.getSubject().getSession().setTimeout(timeout)
    } catch {
      case e: Exception => {
        e.printStackTrace()
        return Right(ErrorDto("Login not successful!"))
      }
    }
    
    val principal = currentUser.getPrincipal().asInstanceOf[User]

    Left(userBuilder.buildDto2(principal, SecurityUtils.getSubject.getSession.getId.toString))
  }
}