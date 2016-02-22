package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.model.User
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.dtos.{PermissionType, ErrorDto, EmptyDto, UserDto}
import com.googlecode.kanbanik.security._

class DeleteUserCommand extends Command[UserDto, EmptyDto] with CredentialsUtils {

  override def execute(params: UserDto, userPerformingAction: User): Either[EmptyDto, ErrorDto] = {
    val user = User.byId(params.userName)

    if (user.unloggedFakeUser) {
      Right(ErrorDto("Can not delete the unloggded user."))
    } else {
      val all = User.all(User().withAllPermissions())
      if (all.filter(!_.unloggedFakeUser).size > 1) {
        // this is an expensive operation - consider to have this info on the user directly to speed things up
        val tasksOfUser = for (board <- Board.all(includeTasks = true, User().withAllPermissions()); task <- board.tasks if task.assignee.getOrElse(User()).name == user.name) yield task
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

  override def checkPermissions(param: UserDto, user: User): Option[List[String]] = {
    doCheckPermissions(user, List(checkOneOf(PermissionType.DeleteUser, param.userName)))
  }
}