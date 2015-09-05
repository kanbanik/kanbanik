package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.model.User
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.dtos.{ErrorDto, EmptyDto, UserDto}

class DeleteUserCommand extends Command[UserDto, EmptyDto] with CredentialsUtils {

  override  def execute(params: UserDto, user: User): Either[EmptyDto, ErrorDto] = {

    if(User.all().size > 1) {
      val user = User.byId(params.userName)

      // this is an expensive operation - consider to have this info on the user directly to speed things up
      val tasksOfUser = for (board <- Board.all(includeTasks = true, user); task <- board.tasks if task.assignee.getOrElse(User()).name == user.name) yield task
      if (tasksOfUser.size == 0) {
        user.copy(version = params.version).delete()
        Left(EmptyDto())
      } else {
        Right(ErrorDto("This user has tasks assigned - please delete them firs. Tasks: " + tasksOfUser.map(_.ticketId).mkString(", ")))
      }
    } else {
      Right(ErrorDto("You can not delete the last user from the system - it would not be possible to log in again!"))
    }
  }
}