package com.googlecode.kanbanik

import com.googlecode.kanbanik.commands.{Command, CreateUserCommand}
import com.googlecode.kanbanik.model.{User, Permission}
import com.googlecode.kanbanik.dtos.PermissionType
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId
import com.mongodb.casbah.Imports._

package object security {

  type Check = PartialFunction[Permission, Boolean]

  type Filter = PartialFunction[Permission, List[Any]]

  type CheckWithMessage = (Check, String)

  def isLogged(user: User): PartialFunction[Permission, Boolean] = {case _ => !user.unloggedFakeUser}
  def isLoggedWithMessage(user: User): (PartialFunction[Permission, Boolean], String) = (isLogged(user), "You need to be logged in to perform this action")


  def checkGlobal(permissionType: PermissionType.Value): (Check, String) = {
    ({case Permission(pt, List()) if pt == permissionType => true}, "Global permission of type: " + permissionType.toString)
  }

  def checkOneOf(permissionType: PermissionType.Value, id: String): (Check, String) = {
    ({case Permission(pt, ids: List[String]) if pt == permissionType => ids.contains(id) || ids.contains("*")}, "Permission: " + permissionType.toString + " on object with ID: " + id + " or on all objects (*)")
  }

  def alwaysPassingCheck(): (Check, String) =
    ({case _ => true}, "")

  def doCheckPermissions(user: User, checks: List[CheckWithMessage]) = {
    val resultsToMessages: List[(Seq[Boolean], String)] = checks.map(check => (user.permissions collect check._1, check._2))
    // the !contains(true) means that it either does not have the permission at all or does not have it for all what I need it for
    val failedMessages = resultsToMessages collect {case rtm if !rtm._1.contains(true) => rtm._2}
    if (failedMessages isEmpty) {
      None
    } else {
      Some(failedMessages)
    }
  }

  type CanReadAll = String

  type PermittedIds = List[Any]

  def buildStringFilterQuery(user: User, pt: PermissionType.Value): MongoDBObject = {
    val conv: String => Any = x => x
    buildFilterQuery(user, pt)(conv) match {
      case Left(ids) => "_id" $in ids
      case Right(_) => MongoDBObject()
    }
  }

  def buildObjectIdFilterQuery(user: User, pt: PermissionType.Value): DBObject = {
    val conv: String => Any = x => new ObjectId(x)
    buildFilterQuery(user, pt)(conv) match {
      case Left(ids) => "_id" $in ids
      case Right(_) => MongoDBObject()
    }
  }

  def canRead(user: User, pt: PermissionType.Value, id: String): Boolean = {
    buildFilterQuery(user, pt)(x => x) match {
      case Left(ids) => ids.contains(id)
      case Right(_) => true
    }
  }

  def buildFilterQuery(user: User, pt: PermissionType.Value)(conv: String => Any): Either[PermittedIds, CanReadAll] = {
    val r = user.permissions.collect {case Permission(realPt, args) if pt == realPt => args}
    val flat = r.flatten
    if (!flat.contains("*")) {
      Left(flat.map(conv))
    } else {
      Right("*")
    }
  }

  def mergePermissions(source: List[Permission], toAdd: List[Permission]): List[Permission] = {

    def f(current: List[Permission], p: Permission): List[Permission] = {
      val alreadyContained = current.find(_.permissionType == p.permissionType)
      if (!alreadyContained.isDefined) {
        p :: current
      } else {
        Permission(p.permissionType, (p.arg ++ alreadyContained.get.arg).distinct) :: current.filterNot(_.permissionType == p.permissionType)
      }
    }

    (source ++ toAdd).foldLeft(List[Permission]())(f)

  }

  // specific helpers
  def checkSavePermissions(user: User,
                           id: Option[String],
                           create: PermissionType.Value,
                           edit: PermissionType.Value): Option[List[String]] = {
    if (id.isDefined) {
      doCheckPermissions(user, List(
        checkOneOf(edit, id.get)
      ))
    } else {
      doCheckPermissions(user, List(
        checkGlobal(create)
      ))
    }
  }

  def checkEditBoardPermissions(user: User, id: Option[String]): Option[List[String]] = {
    // if not, it will fail after on validations
    if (id.isDefined) {
      doCheckPermissions(user, List(
        checkOneOf(PermissionType.EditBoard, id.get)
      ))
    } else {
      None
    }
  }

  def checkIdIfDefined(user: User, id: Option[String], perm: PermissionType.Value): Option[List[String]] = {
    if (id.isDefined) {
      doCheckPermissions(user, List(
        checkOneOf(perm, id.get)
      ))
    } else {
      None
    }
  }

  // this is a race - if someone will edit the entity now the permissions will not be granted
  // ignoring for now because complex locking would be slow and this is not a too big deal - some more powerful admin can
  // fix it by granting the permissions by hand
  def addMePermissions(user: User,
                       oldEntityId: Option[String],
                       entityId: String,
                       permissions: PermissionType.Value*) {

    // e.g. not edit but new, so add this permissions and merge with old ones
    if (!oldEntityId.isDefined) {
      addMePermissions(user, entityId, permissions:_*)
    }
  }

  def addMePermissions(user: User,
                       entityId: String,
                       permissions: PermissionType.Value*) {
      val newPermissions = permissions.map(Permission(_, List(entityId)))
      user.copy(permissions = mergePermissions(user.permissions, newPermissions.toList)).store
  }

}
