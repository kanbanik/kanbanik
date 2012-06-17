package com.googlecode.kanbanik.model

// just a bridge between java and scala world
class KanbanikConnectionManager {
	def destroyConnection = KanbanikEntity.destroyConnection
}