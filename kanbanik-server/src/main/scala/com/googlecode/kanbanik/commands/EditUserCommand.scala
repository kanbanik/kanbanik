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


  override def composeWantsToSet(wantToSet: List[Permission], currentUser: User, editedUser: String): List[Permission] = {
    val user = User.byId(editedUser)
    merge(wantToSet, user.permissions)
  }

  def merge(wantToSet: List[Permission], editedUserPermissions: List[Permission]): List[Permission] = {
    val res: List[Option[Permission]] = for (oneToSet <- wantToSet)
      yield
      if (!editedUserPermissions.find(x => x.permissionType == oneToSet.permissionType).isDefined) {
        Some(oneToSet)
      } else {
        None
      }

    res.filter(_.isDefined).map(_.get)
  }

  override def baseCheck(param: ManipulateUserDto): (Check, String) = checkOneOf(PermissionType.EditUserData, param.userName)

  override def composeFullCheck(param: ManipulateUserDto, baseCheck: (Check, String), allPermissionsIWantToSet: List[(Check, String)]): List[(Check, String)] =
    checkOneOf(PermissionType.EditUserPermissions, param.userName) :: baseCheck :: allPermissionsIWantToSet

}