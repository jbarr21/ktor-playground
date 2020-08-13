package io.github.jbarr21.kotlin

import com.ryanharter.ktor.moshi.moshi
import io.github.jbarr21.kotlin.strava.Athlete
import io.github.jbarr21.kotlin.strava.StravaSession
import io.github.jbarr21.kotlin.util.MoshiSessionSerializer
import io.github.jbarr21.kotlin.util.Response
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.features.*
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.*
import io.ktor.util.hex
import kotlinx.html.*
import org.slf4j.event.Level
import redirectUrl

private const val STRAVA_OAUTH = "strava-oauth"

private val applicationComponent by lazy { DaggerApplicationComponent.create() }

fun main(args: Array<String>) {
  val port = System.getenv()["PORT"]?.toInt() ?: 8080
  embeddedServer(Netty, port = port) {
    module()
  }.start(wait = true)
}

private fun Application.module() {
  install(DefaultHeaders)
  install(CallLogging) {
    level = Level.DEBUG
  }
  install(StatusPages) {
    exception<Throwable> { e ->
      call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
    }
  }
  install(ContentNegotiation) {
    moshi()
  }
  install(Sessions) {
    cookie<StravaSession>("${StravaSession::class.java.simpleName}Id") {
      val secretSignKey = hex(Secrets.STRAVA_SESSION_SIGN_KEY)
      transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
      serializer = MoshiSessionSerializer(applicationComponent.moshi(), StravaSession::class.java)
    }
  }
  install(Authentication) {
    oauth(STRAVA_OAUTH) {
      client = HttpClient(Apache)
      providerLookup = { applicationComponent.stravaOAuth2ServerSettings() }
      urlProvider = { redirectUrl("/strava/login") }
    }
  }
  routing {
    get("/") {
      call.respondText("Hello World!", ContentType.Text.Plain)
    }
    get("/hello") {
      call.respond(Response(status = "Hello, ${call.parameters["name"] ?: "World"}!"))
    }
    get("/covid/cases.json") {
      call.respond(applicationComponent.covidManager().getNewCovidCases())
    }
    get("/strava") {
      call.stravaMainPage()
    }
    authenticate(STRAVA_OAUTH) {
      get("/strava/login") {
        val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>() ?: error("No principal")
        println(principal.extraParameters["athlete"].orEmpty())

        applicationComponent.moshi()
          .adapter(Athlete::class.java)
          .fromJson(principal.extraParameters["athlete"].orEmpty())
          ?.let {
            call.sessions.set(StravaSession(principal.accessToken, it))
          }
        call.respondRedirect("/strava")
      }
    }
    get("/strava/logout") {
      call.sessions.clear<StravaSession>()
      call.response.header("Cache-Control", "no-cache, no-store, must-revalidate")
      call.respondRedirect("/strava")
    }
  }
}

private suspend fun ApplicationCall.stravaMainPage() {
  val session = sessions.get<StravaSession>()
  respondHtml {
    head {
      title { +"Strava Stats Visualizer" }
    }
    body {
      h1 { +"Welcome ${session?.athlete?.name.orEmpty()}" }
      if (session != null) {
        img(src = session.athlete.profile.orEmpty())
        p {
          a(href = "/strava/logout") { +"Logout" }
        }
      } else {
        p { +"Click below to login!" }
        a(href = "/strava/login") { +"Login" }
      }
    }
  }
}


