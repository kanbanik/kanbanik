package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.dtos._
import com.googlecode.kanbanik.dtos.StatusDto
import com.googlecode.kanbanik.dtos.SessionDto
import org.apache.shiro.subject.Subject

class LogoutCommand extends Command[SessionDto, StatusDto] {

  def execute(params: SessionDto): Either[StatusDto, ErrorDto] = {
    val subject = new Subject.Builder().sessionId(params.sessionId).buildSubject
    subject.logout()
    Left(StatusDto(true, None))
  }
  
}