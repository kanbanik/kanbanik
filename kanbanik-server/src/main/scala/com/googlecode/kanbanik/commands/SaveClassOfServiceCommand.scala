package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.ClassOfServiceBuilder
import com.googlecode.kanbanik.dtos.{PermissionType, ErrorDto, ClassOfServiceDto}
import com.googlecode.kanbanik.model.{Permission, User}
import com.googlecode.kanbanik.security._
import org.bson.types.ObjectId

class SaveClassOfServiceCommand extends Command[ClassOfServiceDto, ClassOfServiceDto] {

  lazy val classOfServiceBuilder = new ClassOfServiceBuilder()

  override def execute(params: ClassOfServiceDto, user: User): Either[ClassOfServiceDto, ErrorDto] = {
    val entity = classOfServiceBuilder.buildEntity(params)

    if (!params.id.isDefined) {
      // created, granting all the permissions of it
      addMePermissions(user, entity.id.get.toString)
    }

    Left(classOfServiceBuilder.buildDto(entity.store))
  }

  def addMePermissions(user: User, classOfSerciceId: String) {
    val newPermissions = List(
      Permission(PermissionType.ReadClassOfService, List(classOfSerciceId)),
      Permission(PermissionType.EditClassOfService, List(classOfSerciceId)),
      Permission(PermissionType.DeleteClassOfService, List(classOfSerciceId))
    )

    user.copy(permissions = mergePermissions(user.permissions, newPermissions)).store

  }

  override def checkPermissions(param: ClassOfServiceDto, user: User): Option[List[String]] = {

    if (param.id.isDefined) {
      doCheckPermissions(user, List(
        checkOneOf(PermissionType.EditClassOfService, param.id.get)
      ))
    } else {
      doCheckPermissions(user, List(
        checkGlobal(PermissionType.CreateClassOfService)
      ))
    }


  }
}