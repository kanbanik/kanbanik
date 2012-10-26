package com.googlecode.kanbanik.messages

object ServerMessages {
	val midAirCollisionException = "Mid Air Collistion Exception. Please refresh your browser to get the accurate data."
	def entityDeletedMessage(entity: String) = "This "+entity+" has been deleted by a different user. Please refresh your browser to get the current data."
}