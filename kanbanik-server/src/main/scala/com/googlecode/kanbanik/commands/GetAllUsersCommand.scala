package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.dtos.{ErrorDto, UserDto, SessionDto, ListDto}
import com.googlecode.kanbanik.builders.UserBuilder
import com.googlecode.kanbanik.model.User

class GetAllUsersCommand extends Command[SessionDto, ListDto[UserDto]] with CredentialsUtils {

  def execute(params: SessionDto): Either[ListDto[UserDto], ErrorDto] = {
    Left(ListDto(User.all.map(UserBuilder.buildDto(_, params.sessionId))))
  }
}