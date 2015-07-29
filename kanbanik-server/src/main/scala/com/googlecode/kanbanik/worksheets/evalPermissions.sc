
object PermissionType extends Enumeration {
  val ManipulateBoard = Value(0)
  val ManipulateUser = Value(1)
  val ManipulateProject = Value(2)
}

case class Permission(permissionType: PermissionType.Value, arg: Any*)

case class User(name: String, permissions: Permission*)

type Check = PartialFunction[Permission, Boolean]

type CheckWithMessage = (Check, String)

def checkPermissions(user: User): List[String] = {
  doCheckPermissions(user, List[CheckWithMessage](
    ({case Permission(PermissionType.ManipulateBoard, some: String) => true}, "you need this"),
    ({case Permission(PermissionType.ManipulateUser, some: String) => true}, "you need that")
  ))
}

def doCheckPermissions(user: User, checks: List[CheckWithMessage]) = {
  val resultsToMessages: List[(Seq[Boolean], String)] = checks.map(check => (user.permissions collect check._1, check._2))
  val failedMessages = resultsToMessages collect {case rtm if !rtm._1.contains(true) => rtm._2}
  failedMessages
}

val manipulateProjectPermission = Permission(PermissionType.ManipulateProject)
val manipulateBoardPermission = Permission(PermissionType.ManipulateBoard, "param")
val manipulateUserPermission = Permission(PermissionType.ManipulateUser, "other param")

checkPermissions(User("a", manipulateProjectPermission))

checkPermissions(User("a", manipulateBoardPermission, manipulateProjectPermission))

checkPermissions(User("a", manipulateBoardPermission, manipulateProjectPermission, manipulateUserPermission))


