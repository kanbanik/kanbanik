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


  def doCheckPermissions(user: User, checks: List[CheckWithMessage]) = {
    val resultsToMessages: List[(Seq[Boolean], String)] = checks.map(check => (user.permissions collect check._1, check._2))
    val failedMessages = resultsToMessages collect {case rtm if !rtm._1.contains(true) => rtm._2}
    if (failedMessages isEmpty) {
      None
    } else {
      Some(failedMessages)
    }
  }

  type CanReadAll = String

  type PermittedIds = List[Any]

  def buildObjectIdFilterQuery(user: User, pt: PermissionType.Value): MongoDBObject = {
    val conv: String => Any = x => new ObjectId(x)
    buildFilterQuery(user, pt)(conv) match {
      case Left(ids) => "_id" $in ids
      case Right(_) => MongoDBObject()
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
}
