package com.googlecode.kanbanik.model

// just a bridge between java and scala world
class KanbanikConnectionManager {
	def destroyConnectionPool = HasMongoConnection.destroyConnection
	
	def initConnectionPool(
	  server: String, 
      port: String, 
      user: String, 
      password: String, 
      dbName: String,
      authenticationRequired: String) = HasMongoConnection.initConnectionParams(
          server,
          port,
          user,
          password,
          dbName,
          authenticationRequired
          )
}