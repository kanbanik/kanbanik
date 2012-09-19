package com.googlecode.kanbanik.model

abstract class DocumentField extends Enumeration {
	val id = Value("_id")
	val name = Value("name")
	val version = Value("version")
}