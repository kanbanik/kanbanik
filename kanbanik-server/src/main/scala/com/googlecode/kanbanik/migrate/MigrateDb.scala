package com.googlecode.kanbanik.migrate

import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.commons.MongoDBObject
import com.googlecode.kanbanik.dto.ManipulateUserDto
import com.googlecode.kanbanik.commands.CreateUserCommand
import com.googlecode.kanbanik.dto.shell.SimpleParams

class MigrateDb extends HasMongoConnection {

  val versionMigrations = Map(1 -> List(new From1To2))

  def migrateDbIfNeeded {
    using(createConnection) { conn =>
      val version = coll(conn, Coll.KanbanikVersion).findOne()
      if (version.isDefined) {
        val curVersion = version.get.get("version").asInstanceOf[Int]
        runAllFrom(curVersion)
      } else {
        coll(conn, Coll.KanbanikVersion) += MongoDBObject("version" -> 1)
        runAllFrom(1)
      }

    }
  }
  
  def runAllFrom(curVersion : Int) {
    val migrationParts = versionMigrations.get(curVersion)
    if (migrationParts.isDefined) {
    	for (part <- migrationParts.get) part.migrate
    }
  }

}

trait MigrationPart extends HasMongoConnection {
  def migrate;
  
  def setVersionTo(version: Int) {
    using(createConnection) { conn =>
    	coll(conn, Coll.KanbanikVersion).update(MongoDBObject(),
          $set("version" -> version))
    }
  }
  
}

class From1To2 extends MigrationPart {
  def migrate  {
    
    // create a default user
    val userDto = new ManipulateUserDto(
      "admin",
      "Default User",
      1,
      "admin",
      "admin")

    // create the first user
    new CreateUserCommand().execute(new SimpleParams(userDto))
    
    // add isBalanceWorkflowitems support
    
    setVersionTo(2)
  }
}