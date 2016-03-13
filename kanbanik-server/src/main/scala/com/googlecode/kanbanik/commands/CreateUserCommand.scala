package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.{PermissionsBuilder, UserBuilder}
import com.googlecode.kanbanik.db.HasMongoConnection
import com.googlecode.kanbanik.dtos._
import com.googlecode.kanbanik.model.{Permission, User}
import com.googlecode.kanbanik.security._

class CreateUserCommand extends BaseUserCommand with CredentialsUtils with HasMongoConnection {

  override def execute(params: ManipulateUserDto, currentUser: User): Either[UserDto, ErrorDto] = {

    val name = params.userName

    if (name == null || name == "") {
      return Right(ErrorDto("The user needs to have the name set!"))
    }

    if (User(name).exists()) {
      return Right(ErrorDto("The user with this name already exists!"))
    }

    val permissions = if (params.permissions.isDefined) {
      val incorrectPermissions = findIncorrectPermissions(params.permissions.get)
      if (incorrectPermissions.isDefined) {
        return Right(incorrectPermissions.get)
      }
      params.permissions.get.map(PermissionsBuilder.buildEntity(_))
    } else {
      List()
    }

    val (password, salt) = hashPassword(params.password)
    
    val user = new User(
        name,
    		password,
    		params.realName,
    		params.pictureUrl,
    		salt,
    		1,
        mergePermissions(permissions, List(
          Permission(PermissionType.ReadUser, List(name)),
          Permission(PermissionType.EditUserData, List(name)),
          Permission(PermissionType.DeleteUser, List(name)))),
        false
    ).store

    // the user who has created this one will get full permissions over him
    // in error cases this user may not exist anymore (e.g. race condition). Should not be re-created here.
    if (currentUser.exists()) {
      addMePermissions(currentUser,
        user.name,
        PermissionType.ReadUser,
        PermissionType.EditUserData,
        PermissionType.EditUserPermissions,
        PermissionType.DeleteUser
      )
    }


    new Left(UserBuilder.buildDto(user, params.sessionId.get))
  }


  override def composeWantsToSet(wantToSet: List[Permission], currentUser: User, editedUserName: String): List[Permission] = wantToSet

  override def baseCheck(param: ManipulateUserDto): (Check, String) = checkGlobal(PermissionType.CreateUser)

  override def composeFullCheck(param: ManipulateUserDto, baseCheck: (Check, String), allPermissionsIWantToSet: List[(Check, String)]): List[(Check, String)] =
    baseCheck :: allPermissionsIWantToSet
}