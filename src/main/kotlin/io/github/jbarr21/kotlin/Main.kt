package io.github.jbarr21.kotlin

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

private val applicationComponent by lazy { DaggerApplicationComponent.create() }

fun main(args: Array<String>) {
  val port = System.getenv().let { it["PORT"]?.toInt() ?: 8080 }
  embeddedServer(Netty, port = port) {
    module()
  }.start(wait = true)
}

private fun Application.module() {
  install(DefaultHeaders)
  routing {
    get("/") {
      call.respondText("Hello World!", ContentType.Text.Plain)
    }
    get("/hello") {
      call.respondText("Hello, ${call.parameters["name"] ?: "World"}!")
    }
    get("/covid.json") {
      call.respondText(
        applicationComponent.covidManager().getNewCovidCases(),
        ContentType.Application.Json)
    }
  }
}
