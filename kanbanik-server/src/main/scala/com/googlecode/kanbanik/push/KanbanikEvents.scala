package com.googlecode.kanbanik.push

import org.atmosphere.config.service.{Disconnect, Ready, ManagedService}
import org.atmosphere.cpr._

@ManagedService(path = "/events")
class KanbanikEvents {

    @Ready
    def onReady(r: AtmosphereResource) {


      System.out.println("ready");
    }

    @Disconnect
    def onDisconnect(event: AtmosphereResourceEvent) {
    }

    @org.atmosphere.config.service.Message
    def onMessage(m: String) {
    }
}