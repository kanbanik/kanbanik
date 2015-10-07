import com.googlecode.kanbanik.builders.PermissionsBuilder
import com.googlecode.kanbanik.commands.{BaseUserCommand, EditUserCommand}
import com.googlecode.kanbanik.db.HasMongoConnection
import com.googlecode.kanbanik.dtos.{ErrorDto, PermissionDto, PermissionType, ManipulateUserDto}
import com.googlecode.kanbanik.model.{Permission, User}
import com.googlecode.kanbanik.security._

class EditUserCommand2 extends BaseUserCommand {
  override def checkPermissions(param: ManipulateUserDto, user: User): Option[List[String]] = {
    val editUserCheck = checkOneOf(PermissionType.EditUserData, user.name)

    if (!param.permissions.isDefined) {
      doCheckPermissions(user, List(editUserCheck))
    } else {
      val incorrectPermissions = findIncorrectPermissions(param.permissions.get)
      if (incorrectPermissions.isDefined) {
        // will be handled by the check later
        None
      } else {
        val wantsToSet = param.permissions.get.map(PermissionsBuilder.buildEntity(_))
        // the user can set only permissions (s)he already holds
        val allPermissionsIWantToSet: List[CheckWithMessage] = (for (oneToSet <- wantsToSet)
          yield oneToSet.arg.map(checkOneOf(oneToSet.permissionType, _))).flatten

        doCheckPermissions(user,
          checkOneOf(PermissionType.EditUserPermissions, user.name) :: editUserCheck :: allPermissionsIWantToSet
        )
      }
    }

  }
}


def doCheckPermissions(user: User, checks: List[(PartialFunction[Permission, Boolean], String)]) = {
  val resultsToMessages: List[(Seq[Boolean], String)] = checks.map(check => (user.permissions collect check._1, check._2))
  val failedMessages = resultsToMessages collect {case rtm if !rtm._1.contains(true) => rtm._2}
  if (failedMessages isEmpty) {
    None
  } else {
    Some(failedMessages)
  }
}


def findIncorrectPermissions(permissions: List[PermissionDto]): Option[ErrorDto] = {
  val ids = for (t <- PermissionType.values) yield t.id
  val res = permissions.filter(p => !ids.contains(p.permissionType))
  if (res.isEmpty) None else Some(ErrorDto("Unknown permission ids: " + res.map(_.permissionType).mkString(", ")))
}

def checkPermissions(param: ManipulateUserDto, user: User): Option[List[String]] = {
  val editUserCheck = checkOneOf(PermissionType.EditUserData, user.name)

  if (!param.permissions.isDefined) {
    doCheckPermissions(user, List(editUserCheck))
  } else {
    val incorrectPermissions = findIncorrectPermissions(param.permissions.get)
    if (incorrectPermissions.isDefined) {
      // will be handled by the check later
      None
    } else {
      val wantsToSet = param.permissions.get.map(PermissionsBuilder.buildEntity(_))
      // the user can set only permissions (s)he already holds
      val allPermissionsIWantToSet: List[CheckWithMessage] = (for (oneToSet <- wantsToSet)
        yield oneToSet.arg.map(checkOneOf(oneToSet.permissionType, _))).flatten

      doCheckPermissions(user,
        List(checkOneOf(PermissionType.EditUserPermissions, user.name))
//          :: editUserCheck :: allPermissionsIWantToSet
      )
    }
  }

}

def x() = {
  HasMongoConnection.dbName = "kanbanikdb"

  val user = User.byId("u")

  val manipulateUser = ManipulateUserDto(
    "admin",
    "real name",
    "",
    None,
    1,
    "",
    "",
//    None
    Some(List(PermissionDto(1, List("u"))))
  )

  val cmd = new EditUserCommand()

  checkPermissions(manipulateUser, user)

  doCheckPermissions(user,
    List(checkOneOf(PermissionType.EditUserPermissions, user.name))
    //          :: editUserCheck :: allPermissionsIWantToSet
  )




  val resultsToMessages: List[(Seq[Boolean], String)] = List(checkOneOf(PermissionType.EditUserPermissions, user.name)).map(check => (user.permissions collect check._1, check._2))
  val failedMessages = resultsToMessages collect {case rtm if !rtm._1.contains(true) => rtm._2}
//  if (failedMessages isEmpty) {
//    None
//  } else {
//    Some(failedMessages)
//  }
//  resultsToMessages

  failedMessages


//  user.permissions


}

x()

(1 to 10) collect { case x if x % 2 == 0 => x}