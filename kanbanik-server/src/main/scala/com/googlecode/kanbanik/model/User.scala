package com.googlecode.kanbanik.model

import com.googlecode.kanbanik.db.{HasMidAirCollisionDetection, HasMongoConnection}
import com.googlecode.kanbanik.commons._
import com.googlecode.kanbanik.dtos.PermissionType
import com.googlecode.kanbanik.model.Permission
import com.googlecode.kanbanik.security._
import com.mongodb.{BasicDBList, DBObject}
import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._

case class User(
  name: String,
  password: String,
  realName: String,
  pictureUrl: String,
  salt: String,
  version: Int,
  permissions: List[Permission],
  unloggedFakeUser: Boolean
  ) extends HasMongoConnection with HasMidAirCollisionDetection {

  def store: User = {
      if (exists()) {
        update
      } else {
        createNew
      }
  }
  
  def exists() = {
    using(createConnection) { conn =>
      val user = coll(conn, Coll.Users).findOne(MongoDBObject(Task.Fields.id.toString -> name))
      user.isDefined
    }
  }
  
  private def createNew = {
    val obj = User.asDBObject(this)
    using(createConnection) { conn =>
      coll(conn, Coll.Users) += obj
    }

    User.asEntity(obj)
  }

  private def update = {
    val update = $set(
      User.Fields.name.toString -> name,
      User.Fields.password.toString -> password,
      User.Fields.realName.toString -> realName,
      User.Fields.pictureUrl.toString -> {if (pictureUrl == null) "" else pictureUrl},
      User.Fields.salt.toString -> salt,
      User.Fields.permissions.toString -> User.asPermissionsDbObject(this),
      User.Fields.version.toString -> { version + 1 })

    User.asEntity(versionedUpdate(Coll.Users, versionedQuery(name, version), update))

  }

  def delete() {
    versionedDelete(Coll.Users, versionedQuery(name, version))
  }

  def withAllPermissions(): User = {
    copy(permissions = User.allPermissions)
  }
}

object User extends HasMongoConnection {

  val allPermissions = List(

    Permission(PermissionType.ReadProject, List("*")),
    Permission(PermissionType.EditProject, List("*")),
    Permission(PermissionType.DeleteProject, List("*")),
    Permission(PermissionType.CreateProject, List()),

    Permission(PermissionType.ReadUser, List("*")),

    Permission(PermissionType.CreateClassOfService, List()),
    Permission(PermissionType.ReadClassOfService, List("*")),
    Permission(PermissionType.EditClassOfService, List("*")),
    Permission(PermissionType.DeleteClassOfService, List("*")),

    Permission(PermissionType.CreateBoard, List()),
    Permission(PermissionType.ReadBoard, List("*")),
    Permission(PermissionType.EditBoard, List("*")),
    Permission(PermissionType.DeleteBoard, List("*")),

    Permission(PermissionType.EditUserData, List("*")),
    Permission(PermissionType.EditUserPermissions, List("*")),
    Permission(PermissionType.DeleteUser, List("*")),
    Permission(PermissionType.CreateUser, List()),

    Permission(PermissionType.CreateTask_p, List("*")),
    Permission(PermissionType.EditTask_p, List("*")),
    Permission(PermissionType.DeleteTask_p, List("*")),
    Permission(PermissionType.MoveTask_p, List("*")),

    Permission(PermissionType.CreateTask_b, List("*")),
    Permission(PermissionType.EditTask_b, List("*")),
    Permission(PermissionType.DeleteTask_b, List("*")),
    Permission(PermissionType.MoveTask_b, List("*"))
  )

  object Fields extends DocumentField {
    val realName = Value("realName")
    val pictureUrl = Value("pictureUrl")
    val password = Value("password")
    val salt = Value("salt")
    val permissions = Value("permissions")
    // the unlogged fake user
    val unlogged = Value("unlogged")
  }

  val UNLOGGED = "unlogged"

  def apply(): User = User("", "", "", "", "", 1, List(), false)

  def apply(name: String): User = User(name, "", "", "", "", 1, List(), false)

  def all(user: User): List[User] = {
    using(createConnection) { conn =>
      coll(conn, Coll.Users).find(buildStringFilterQuery(user, PermissionType.ReadUser)).sort(MongoDBObject(User.Fields.name.toString -> 1)).map(asEntity).toList
    }
  }

  def unlogged: User = {
    using(createConnection) { conn =>
      val dbUser = coll(conn, Coll.Users).findOne(MongoDBObject(Fields.id.toString -> UNLOGGED))

      if(dbUser.isDefined)  {
        asEntity(dbUser.get)
      } else {
        User(UNLOGGED).copy(
          unloggedFakeUser = true,
          realName = "Unlogged User",
          version = 1
        ).store
      }
    }
  }

  def byId(name: String) = {
    using(createConnection) { conn =>
      val dbUser = coll(conn, Coll.Users).findOne(MongoDBObject(Fields.id.toString -> name)).getOrElse(throw new IllegalArgumentException("No such user with name: " + name))
      asEntity(dbUser)
    }

  }

  private def asEntity(dbObject: DBObject) = {
    User(
      dbObject.get(Fields.id.toString).asInstanceOf[String],
      dbObject.get(Fields.password.toString).asInstanceOf[String],
      dbObject.get(Fields.realName.toString).asInstanceOf[String],
      dbObject.get(Fields.pictureUrl.toString).asInstanceOf[String],
      dbObject.get(Fields.salt.toString).asInstanceOf[String],
      dbObject.get(Fields.version.toString).asInstanceOf[Int],
      asPermissions(dbObject),
      dbObject.getWithDefault[Boolean](Fields.unlogged, false))
  }

  private def asDBObject(entity: User): DBObject = {
    MongoDBObject(
      Fields.id.toString -> entity.name,
      Fields.password.toString -> entity.password,
      Fields.realName.toString -> entity.realName,
      Fields.pictureUrl.toString -> {if (entity.pictureUrl == null) "" else entity.pictureUrl} ,
      Fields.salt.toString -> entity.salt,
      Fields.permissions.toString -> asPermissionsDbObject(entity),
      Fields.version.toString -> entity.version,
      Fields.unlogged.toString -> entity.unloggedFakeUser)

  }

  def asPermissionsDbObject(entity: User): List[DBObject] = {
    if (entity.permissions != null) entity.permissions.map(asPermissionDbObject(_)) else List()
  }

  object PermissionFields extends DocumentField {
    val params = Value("params")
    val value = Value("value")
  }

  private def asPermissionDbObject(permission: Permission): DBObject = {
    MongoDBObject(
      PermissionFields.id.toString -> permission.permissionType.id,
      PermissionFields.params.toString -> permission.arg.map(value => MongoDBObject(PermissionFields.value.toString -> value))
    )
  }

  private def asPermissions(dbObject: DBObject): List[Permission] = {
    val permissions = dbObject.get(Fields.permissions.toString)
    if (permissions != null && permissions.isInstanceOf[BasicDBList]) {
      val list = dbObject.get(Fields.permissions.toString).asInstanceOf[BasicDBList].toArray.toList.asInstanceOf[List[DBObject]]
      list.map(asPermissionEntity(_))
    } else {
      List()
    }
  }

  private def asPermissionEntity(dbObject: DBObject): Permission = {
    Permission(
      PermissionType(dbObject.get(PermissionFields.id.toString).asInstanceOf[Int]),
      asPermissionParams(dbObject)
    )
  }

  private def asPermissionParams(dbObject: DBObject): List[String] = {
    val params = dbObject.get(PermissionFields.params.toString)
    if (params != null && params.isInstanceOf[BasicDBList]) {
      val list = dbObject.get(PermissionFields.params.toString).asInstanceOf[BasicDBList].toArray.toList.asInstanceOf[List[DBObject]]
      list.map(_.get(PermissionFields.value.toString).asInstanceOf[String])
    } else {
      List()
    }
  }

}

case class Permission(permissionType: PermissionType.Value, arg: List[String])