package com.googlecode.kanbanik

object Configuration {
  var enableGzipCommunication = true

  def init(enableGzipCommunication: Boolean) {
    this.enableGzipCommunication = enableGzipCommunication
  }
}
