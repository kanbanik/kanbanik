import com.googlecode.kanbanik.db.HasMongoConnection
import com.googlecode.kanbanik.dtos.PermissionType
import com.googlecode.kanbanik.model.Board._
import com.mongodb.casbah.Imports._
import com.googlecode.kanbanik.model.{Permission, User, Board}
import com.mongodb.casbah.commons.MongoDBObject

import com.googlecode.kanbanik.security._
import com.googlecode.kanbanik.dtos._
import org.bson.types.ObjectId


//db.users.find({_id: {$in: ["aaaa", "some"]}})
class Some extends HasMongoConnection {
  def load(ids: List[Any]): String = {

    HasMongoConnection.dbName = "kanbanikdb"

    using(createConnection) { conn =>

      val taskExclusionObject = MongoDBObject()

//      val ids: List[Any] = List(
//        new ObjectId("555e1f0de5e05d6d4ca2a83e"),
//        new ObjectId("55e74298e5e089200feedc9a")
//      )

      val s = Board.Fields.id.toString $in ids

      val res = coll(conn, Coll.Boards).find(s, taskExclusionObject).sort(MongoDBObject(Board.Fields.name.toString -> 1)).map(asEntity).toList

      res.map(b => b.id + " " + b.name).mkString(" ------x------ ")
    }
  }


}

print("asd")



def buildObjectIdFilterQuery(user: User, pt: PermissionType.Value): List[Any] = {
  val conv: String => Any = x => new ObjectId(x)
  buildFilterQuery(user, pt)(conv)
}
def buildFilterQuery(user: User, pt: PermissionType.Value)(conv: String => Any): List[Any] = {
  val r = user.permissions.collect {case Permission(realPt, args) if pt == realPt => args.map(conv)}
  r.flatten
}

val manipulateUserPermission = List(
  Permission(
    PermissionType.ReadBoard, List("55e74298e5e089200feedc9a")
  ),

  Permission(
    PermissionType.ManipulateUser, List("555e1f0de5e05d6d4ca2a83e", "55e74298e5e089200feedc9a")
  )
)
val u = User().copy(permissions = manipulateUserPermission)
//

var l = buildObjectIdFilterQuery(u, PermissionType.ReadBoard)

val x = new Some().load(l)

