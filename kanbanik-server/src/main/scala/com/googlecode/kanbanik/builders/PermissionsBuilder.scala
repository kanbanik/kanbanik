package com.googlecode.kanbanik.builders

import com.googlecode.kanbanik.dtos.{PermissionType, PermissionDto}
import com.googlecode.kanbanik.model.Permission

object PermissionsBuilder {

  def buildDto(permission: Permission) = new PermissionDto(permission.permissionType.id, permission.arg)

  def buildEntity(permissionDto: PermissionDto) = Permission(PermissionType(permissionDto.permissionType), permissionDto.args)
}
