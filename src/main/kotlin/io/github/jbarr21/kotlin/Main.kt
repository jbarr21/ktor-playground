package io.github.jbarr21.kotlin

import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
  embeddedServer(Netty, port = System.getenv()["PORT"]?.toInt() ?: 8080) {
    DaggerAppComponent.builder()
      .application(this)
      .build()
      .app()
      .mainModule(this)
  }.start(wait = true)
}
