package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.dto.shell.Params
import com.googlecode.kanbanik.dto.shell.Result

trait ServerCommand[P <: Params, R <: Result] {
	def execute(params: P) : R;
}