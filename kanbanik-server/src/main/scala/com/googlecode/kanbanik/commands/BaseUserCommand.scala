package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.PermissionsBuilder
import com.googlecode.kanbanik.dtos._
import com.googlecode.kanbanik.model.{Permission, User}
import com.googlecode.kanbanik.security._

abstract class BaseUserCommand extends Command[ManipulateUserDto, UserDto] with CredentialsUtils {

  def findIncorrectPermissions(permissions: List[PermissionDto]): Option[ErrorDto] = {
    val ids = for (t <- PermissionType.values) yield t.id
    val res = permissions.filter(p => !ids.contains(p.permissionType))
    if (res.isEmpty) None else Some(ErrorDto("Unknown permission ids: " + res.map(_.permissionType).mkString(", ")))
  }

  override def checkPermissions(param: ManipulateUserDto, user: User): Option[List[String]] = {
    if (!param.permissions.isDefined) {
      doCheckPermissions(user, List(baseCheck(param)))
    } else {
      val incorrectPermissions = findIncorrectPermissions(param.permissions.get)
      if (incorrectPermissions.isDefined) {
        // will be handled by the check later
        None
      } else {
        val wantsToSet = param.permissions.get.map(PermissionsBuilder.buildEntity(_))
        // the user can set only permissions (s)he already holds
        val allPermissionsIWantToSet: List[CheckWithMessage] =
          (for (oneToSet <- wantsToSet) yield {
              if (oneToSet.arg.isEmpty) {
                List(checkGlobal(oneToSet.permissionType))
              } else {
                oneToSet.arg.map(checkOneOf(oneToSet.permissionType, _))
              }
            }).flatten

        doCheckPermissions(user,
          composeFullCheck(param, baseCheck(param), allPermissionsIWantToSet)
        )
      }
    }
  }

  def baseCheck(param: ManipulateUserDto): CheckWithMessage

  def composeFullCheck(param: ManipulateUserDto, baseCheck: CheckWithMessage, allPermissionsIWantToSet: List[CheckWithMessage]): List[CheckWithMessage]

}
