package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.{PermissionsBuilder, UserBuilder}
import com.googlecode.kanbanik.model.User
import com.googlecode.kanbanik.dtos._

class EditUserCommand extends Command[ManipulateUserDto, UserDto] with CredentialsUtils {

  def execute(params: ManipulateUserDto): Either[UserDto, ErrorDto] = {
    if (params.password == "") {
      return Right(ErrorDto("The password can not be empty!"))
    }

    if (!isAuthenticated(params.userName, params.password)) {
      return Right(ErrorDto("The credentials provided are not correct!"))
    } else {
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

      val (resPassword, resSalt): (String, String) = hashPassword(params.newPassword)

      val newUser = user.copy(
        password = resPassword,
        salt = resSalt,
        realName = params.realName,
        version = params.version,
        pictureUrl = params.pictureUrl,
        permissions = newPermissions
      ).store

      new Left(UserBuilder.buildDto(newUser, params.sessionId))
    }
  }

  def findIncorrectPermissions(permissions: List[PermissionDto]): Option[ErrorDto] = {
    val ids = for (t <- PermissionType.values) yield t.id
    val res = permissions.filter(p => !ids.contains(p.permissionType))
    if (res.isEmpty) None else Some(ErrorDto("Unknown permission ids: " + res.map(_.permissionType).mkString(", ")))
  }
}