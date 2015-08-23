package com.googlecode.kanbanik

import com.googlecode.kanbanik.commands.{Command, CreateUserCommand}
import com.googlecode.kanbanik.model.{User, Permission}

package object security {

  type Check = PartialFunction[Permission, Boolean]

  type CheckWithMessage = (Check, String)

  def isLogged(user: User): PartialFunction[Permission, Boolean] = {case _ => !user.unloggedFakeUser}
  def isLoggedWithMessage(user: User): (PartialFunction[Permission, Boolean], String) = (isLogged(user), "You need to be logged in to perform this action")


  def doCheckPermissions(user: User, checks: List[CheckWithMessage]) = {
    val resultsToMessages: List[(Seq[Boolean], String)] = checks.map(check => (user.permissions collect check._1, check._2))
    val failedMessages = resultsToMessages collect {case rtm if !rtm._1.contains(true) => rtm._2}
    if (failedMessages isEmpty) {
      None
    } else {
      Some(failedMessages)
    }
  }

}
