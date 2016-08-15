package com.googlecode.kanbanik

object Configuration {
  var enableGzipCommunication = true
  var enableAccessControlHeaders = false

  def init(enableGzipCommunication: Boolean, enableAccessControlHeaders: Boolean) {
    this.enableGzipCommunication = enableGzipCommunication
    this.enableAccessControlHeaders = enableAccessControlHeaders
  }
}
