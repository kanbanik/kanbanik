package com.googlecode.kanbanik.model

case class Permission(permissionType: PermissionType.Value, arg: List[String])

object PermissionType extends Enumeration {
  val ManipulateBoard = Value(0)
  val ManipulateUser = Value(1)
  val ManipulateProject = Value(2)
}

