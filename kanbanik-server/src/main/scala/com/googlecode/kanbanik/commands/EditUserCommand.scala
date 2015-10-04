package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.{PermissionsBuilder, UserBuilder}
import com.googlecode.kanbanik.dtos._
import com.googlecode.kanbanik.model.{Permission, User}
import com.googlecode.kanbanik.security._

class EditUserCommand extends BaseUserCommand with CredentialsUtils {

  override def execute(params: ManipulateUserDto): Either[UserDto, ErrorDto] = {
    val user = User.byId(params.userName)

    val newPermissions = if (params.permissions.isDefined) {
      val incorrectPermissions = findIncorrectPermissions(params.permissions.get)
      if (incorrectPermissions.isDefined) {
        return Right(incorrectPermissions.get)
      }
      params.permissions.get.map(PermissionsBuilder.buildEntity(_))
    } else {
      user.permissions
    }

    val (resPassword, resSalt): (String, String) = if (
      params.newPassword != null && params.newPassword != "" &&
      !user.unloggedFakeUser
    ) {
      hashPassword(params.newPassword)
    } else {
      (user.password, user.salt)
    }

    val newUser = user.copy(
      password = resPassword,
      salt = resSalt,
      realName = params.realName,
      version = params.version,
      pictureUrl = params.pictureUrl,
      permissions = newPermissions
    ).store

    new Left(UserBuilder.buildDto(newUser, params.sessionId.get))
  }

  override def checkPermissions(param: ManipulateUserDto, user: User): Option[List[String]] = {
    val editUserCheck = checkOneOf(PermissionType.EditUserData, user.name)

    if (!param.permissions.isDefined) {
      doCheckPermissions(user, List(editUserCheck))
    } else {
      val incorrectPermissions = findIncorrectPermissions(param.permissions.get)
      if (incorrectPermissions.isDefined) {
        // will be handled by the check later
        None
      } else {
        val wantsToSet = param.permissions.get.map(PermissionsBuilder.buildEntity(_))
        // the user can set only permissions (s)he already holds
        val allPermissionsIWantToSet: List[CheckWithMessage] = (for (oneToSet <- wantsToSet)
          yield oneToSet.arg.map(checkOneOf(oneToSet.permissionType, _))).flatten

        doCheckPermissions(user,
          checkOneOf(PermissionType.EditUserPermissions, user.name) :: editUserCheck :: allPermissionsIWantToSet
        )
      }
    }

  }

}