package com.googlecode.kanbanik.messages

object ServerMessages {
	val midAirCollisionException = "This item has been modified by a different user. Please refresh your browser to get the current data."
	def entityDeletedMessage(entity: String) = "This "+entity+" has been deleted by a different user. Please refresh your browser to get the current data."
}