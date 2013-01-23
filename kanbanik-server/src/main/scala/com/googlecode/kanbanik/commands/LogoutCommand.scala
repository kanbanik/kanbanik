package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.dto.shell.VoidParams
import org.apache.shiro.SecurityUtils

class LogoutCommand extends ServerCommand[VoidParams, VoidParams] {

  def execute(params: VoidParams): VoidParams = {
    SecurityUtils.getSubject().logout()
    new VoidParams
  }
  
}