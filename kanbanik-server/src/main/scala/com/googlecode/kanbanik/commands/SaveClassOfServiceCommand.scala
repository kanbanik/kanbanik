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

    val res = entity.store

    addMePermissions(user, params.id,
      res.id.get.toString,
      PermissionType.ReadClassOfService,
      PermissionType.EditClassOfService,
      PermissionType.DeleteClassOfService)

    Left(classOfServiceBuilder.buildDto(res))
  }

  override def checkPermissions(param: ClassOfServiceDto, user: User): Option[List[String]] =
    checkSavePermissions(user, param.id, PermissionType.CreateClassOfService, PermissionType.EditClassOfService)

}