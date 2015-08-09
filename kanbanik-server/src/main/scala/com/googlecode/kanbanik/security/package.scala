package com.googlecode.kanbanik

import com.googlecode.kanbanik.commands.{Command, CreateUserCommand}
import com.googlecode.kanbanik.dtos.ManipulateUserDto
import com.googlecode.kanbanik.model.{PermissionType, User, Permission}

package object security {

  type Check = PartialFunction[Permission, Boolean]

  type CheckWithMessage = (Check, String)

  def isLogged(user: User): PartialFunction[Permission, Boolean] = {case _ => !user.unloggedFakeUser}
  def isLoggedWithMessage(user: User): (PartialFunction[Permission, Boolean], String) = (isLogged(user), "You need to be logged in to perform this action")

  case class WithAuth(user: User) {
    def checkPermissions(command: CreateUserCommand, param: ManipulateUserDto): Option[List[String]] = {
      doCheckPermissions(user, List[CheckWithMessage](
        ({case Permission(PermissionType.ManipulateUser, List()) => true}, "You need to have the global Manipulate User permission"),
        isLoggedWithMessage(user)
      ))
    }

    def doCheckPermissions(user: User, checks: List[CheckWithMessage]) = {
      val resultsToMessages: List[(Seq[Boolean], String)] = checks.map(check => (user.permissions collect check._1, check._2))
      val failedMessages = resultsToMessages collect {case rtm if !rtm._1.contains(true) => rtm._2}
      if (failedMessages isEmpty) {
        None
      } else {
        Some(failedMessages)
      }
    }

    def checkPermissions[T, R](command: Command[T, R], param: T): Option[List[String]] = {
      // temporarily bypass permission check for all commands
      // it will stay this way only until the development of this part will be done - never release this way
      None
    }
  }

  implicit def toAuth(user: User) = WithAuth(user)

}
