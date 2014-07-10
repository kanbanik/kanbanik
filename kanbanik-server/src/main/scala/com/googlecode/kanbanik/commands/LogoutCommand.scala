package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.dtos._
import com.googlecode.kanbanik.dtos.StatusDto
import com.googlecode.kanbanik.dtos.SessionDto
import org.apache.shiro.subject.Subject
import org.apache.shiro.SecurityUtils

class LogoutCommand extends Command[SessionDto, StatusDto] {

  def execute(params: SessionDto): Either[StatusDto, ErrorDto] = {
    val sessionId = params.sessionId

    val subject = new Subject.Builder().sessionId(sessionId).buildSubject

    subject.logout()
    Left(StatusDto(true, None))
  }
  
}
