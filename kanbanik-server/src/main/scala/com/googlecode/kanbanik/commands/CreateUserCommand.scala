package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.{PermissionsBuilder, UserBuilder}
import com.googlecode.kanbanik.model.{Permission, User}
import com.googlecode.kanbanik.db.HasMongoConnection
import com.googlecode.kanbanik.dtos._
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
        mergePermissions(permissions, List(Permission(PermissionType.ReadUser, List(name)))),
        false
    ).store

    // this is a race - if someomne will edit the currentUser now the permissions will not be granted
    // ignoring for now because complex locking would be slow and this is not a too big deal - somne more powerful admin can
    // fix it by granting the permissions by hand
    addMePermissions(user, currentUser)

    new Left(UserBuilder.buildDto(user, params.sessionId.get))
  }

  /**
   * When created a user, I need to see who did I create
   */
  def addMePermissions(createdUser: User, currentUser: User) {
    val newPermkissions = List(
      Permission(PermissionType.ReadUser, List(createdUser.name)),
      Permission(PermissionType.EditUserData, List(createdUser.name)),
      Permission(PermissionType.EditUserPermissions, List(createdUser.name)),
      Permission(PermissionType.DeleteUser, List(createdUser.name))
    )

    currentUser.copy(permissions = mergePermissions(currentUser.permissions, newPermkissions)).store

  }

  override def baseCheck(param: ManipulateUserDto): (Check, String) = checkGlobal(PermissionType.CreateUser)

  override def composeFullCheck(param: ManipulateUserDto, baseCheck: (Check, String), allPermissionsIWantToSet: List[(Check, String)]): List[(Check, String)] =
    baseCheck :: allPermissionsIWantToSet
}