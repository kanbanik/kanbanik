package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.dto.ClassOfServiceDto
import com.googlecode.kanbanik.dto.ListDto
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.model.ClassOfService
import com.googlecode.kanbanik.builders.ClassOfServiceBuilder
import com.googlecode.kanbanik.commons._

class GetAllClassOfServices extends ServerCommand[VoidParams, SimpleParams[ListDto[ClassOfServiceDto]]] {

  val builder = new ClassOfServiceBuilder

  def execute(params: VoidParams): SimpleParams[ListDto[ClassOfServiceDto]] = {
    val dtos = ClassOfService.all.map(builder.buildDto(_))
    
    new SimpleParams(new ListDto(dtos.toJavaList))
  }
}