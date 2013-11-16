package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.dto.shell.Params
import com.googlecode.kanbanik.dto.shell.Result

trait ServerCommand[P, R ] {
	def execute(params: P) : R;
}