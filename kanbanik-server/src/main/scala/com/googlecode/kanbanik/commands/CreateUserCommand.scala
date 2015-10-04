package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.{PermissionsBuilder, UserBuilder}
import com.googlecode.kanbanik.model.{Permission, User}
import com.googlecode.kanbanik.db.HasMongoConnection
import com.googlecode.kanbanik.dtos._
import com.googlecode.kanbanik.security._


class CreateUserCommand extends BaseUserCommand with CredentialsUtils with HasMongoConnection {

  override def execute(params: ManipulateUserDto): Either[UserDto, ErrorDto] = {

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
        permissions,
        false
    ).store
    
    new Left(UserBuilder.buildDto(user, params.sessionId.get))
  }

  override def checkPermissions(param: ManipulateUserDto, user: User): Option[List[String]] = {
    doCheckPermissions(user, List[CheckWithMessage](
      checkGlobal(PermissionType.CreateUser)
    ))
  }
}