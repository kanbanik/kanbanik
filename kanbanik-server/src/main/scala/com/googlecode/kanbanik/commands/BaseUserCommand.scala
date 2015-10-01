package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.dtos._

class BaseUserCommand extends Command[ManipulateUserDto, UserDto] with CredentialsUtils {

  def findIncorrectPermissions(permissions: List[PermissionDto]): Option[ErrorDto] = {
    val ids = for (t <- PermissionType.values) yield t.id
    val res = permissions.filter(p => !ids.contains(p.permissionType))
    if (res.isEmpty) None else Some(ErrorDto("Unknown permission ids: " + res.map(_.permissionType).mkString(", ")))
  }

}
