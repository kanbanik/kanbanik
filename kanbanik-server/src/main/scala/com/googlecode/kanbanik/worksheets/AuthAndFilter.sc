import com.googlecode.kanbanik.model.{User, Board}

val res = Board.all(false, false, None, None, User().withAllPermissions())

print(res.map(_.name).mkString(","))