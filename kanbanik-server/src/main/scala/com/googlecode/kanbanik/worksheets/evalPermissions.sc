import com.googlecode.kanbanik.dtos.{PermissionType, PermissionDto, ErrorDto}
import com.googlecode.kanbanik.model.{Permission}
//object PermissionType extends Enumeration {
//  val ManipulateBoard = Value(0)
//  val ManipulateUser = Value(1)
//  val ManipulateProject = Value(2)
//}

case class User(name: String, permissions: Permission*)
type Check = PartialFunction[Permission, Boolean]
type CheckWithMessage = (Check, String)
def checkPermissions(user: User): Option[List[String]] = {
  doCheckPermissions(user, List[CheckWithMessage](
    checkOneOf(PermissionType.ManipulateBoard, "param")
  ))
}

def checkOneOf(permissionType: PermissionType.Value, id: String): (Check, String) = {
  ({case Permission(permissionType, ids: List[String]) => ids.contains(id) || ids.contains("*")}, permissionType.toString)
}

def doCheckPermissions(user: User, checks: List[CheckWithMessage]) = {
  println("-->" + (user.permissions collect checks.head._1))

  val resultsToMessages: List[(Seq[Boolean], String)] = checks.map(check => (user.permissions collect check._1, check._2))

  println("->" + resultsToMessages.mkString(", "))

  val failedMessages = resultsToMessages collect {case rtm if !rtm._1.contains(true) => rtm._2}
  if (failedMessages isEmpty) {
    None
  } else {
    Some(failedMessages)
  }
}


val manipulateProjectPermission = Permission(PermissionType.ManipulateProject, List())
val manipulateBoardPermission = Permission(PermissionType.ManipulateBoard, List("param2"))
//val manipulateUserPermission = Permission(PermissionType.ManipulateUser, "other param")

//checkOneOf(PermissionType.ManipulateBoard, "param")._1.isDefinedAt(manipulateBoardPermission)
//val resultsToMessages: List[(Seq[Boolean], String)] = List(
//  checkOneOf(PermissionType.ManipulateBoard, "param"),
//    checkOneOf(PermissionType.ManipulateBoard, "param2")
//
//).map(
//  check => (List(manipulateBoardPermission, manipulateProjectPermission) collect check._1, check._2)
//)
//
//val failedMessages = resultsToMessages collect {case rtm if !rtm._1.contains(true) => rtm._2}

//checkPermissions(User("a", manipulateProjectPermission))
//
//checkPermissions(User("a", manipulateBoardPermission, manipulateProjectPermission))

println("before")
println("RRRes = " + checkPermissions(User("a", manipulateBoardPermission, manipulateProjectPermission)).mkString(", "))
println("end")

//findIncorrectPermissions(List(new PermissionDto(1, List()), new PermissionDto(9, List()), new PermissionDto(8, List())))
//
//def findIncorrectPermissions(permissions: List[PermissionDto]): Option[ErrorDto] = {
//  val ids = for (t <- PermissionType.values) yield t.id
//  val res = permissions.filter(p => !ids.contains(p.permissionType))
//  if (res.isEmpty) None else Some(ErrorDto("Unknown permission ids: " + res.map(_.permissionType).mkString(", ")))
//}


