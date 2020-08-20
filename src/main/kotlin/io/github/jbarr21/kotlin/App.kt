package io.github.jbarr21.kotlin

import com.mitchellbosecke.pebble.loader.ClasspathLoader
import com.ryanharter.ktor.moshi.moshi
import com.squareup.moshi.Moshi
import io.github.jbarr21.kotlin.covid.CovidManager
import io.github.jbarr21.kotlin.strava.StravaManager
import io.github.jbarr21.kotlin.strava.StravaManager.Companion.STRAVA_OAUTH
import io.github.jbarr21.kotlin.strava.StravaManager.Companion.redirectUnauthenticatedStravaRoutes
import io.github.jbarr21.kotlin.strava.model.StravaSession
import io.github.jbarr21.kotlin.util.MoshiSessionSerializer
import io.github.jbarr21.kotlin.util.Response
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.pebble.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.*
import org.slf4j.event.Level
import redirectUrl
import javax.inject.Inject

class App @Inject constructor(
  private val covidManager: CovidManager,
  private val moshi: Moshi,
  private val stravaManager: StravaManager,
  private val stravaOAuth2ServerSettings: OAuthServerSettings.OAuth2ServerSettings
) {

  fun mainModule(application: Application) = application.apply {
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
        serializer = MoshiSessionSerializer(moshi, StravaSession::class.java)
      }
    }
    install(Authentication) {
      oauth(STRAVA_OAUTH) {
        client = HttpClient(Apache)
        providerLookup = { stravaOAuth2ServerSettings }
        urlProvider = { redirectUrl("/strava/login") }
      }
    }
    install(Pebble) { // this: PebbleEngine.Builder
      loader(ClasspathLoader().apply {
        prefix = "templates"
      })
    }
    routing {
      get("/") {
        call.respondText("Hello World!", ContentType.Text.Plain)
      }
      get("/hello") {
        call.respond(Response(status = "Hello, ${call.parameters["name"] ?: "World"}!"))
      }
      get("/covid/cases.json") {
        call.respond(covidManager.getNewCovidCases())
      }
      get("/strava") {
        call.sessions.get<StravaSession>()?.let { session ->
          stravaManager.mainPage(call, session)
        } ?: call.respond(PebbleContent("login.peb", emptyMap()))
      }
      get("/strava/charts") {
        call.respondPlaceholderStravaPage()
      }
      get("/strava/profile") {
        call.respondPlaceholderStravaPage()
      }
      get("/strava/settings") {
        call.respondPlaceholderStravaPage()
      }
      redirectUnauthenticatedStravaRoutes()
      authenticate(STRAVA_OAUTH) {
        get("/strava/login") {
          call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()?.let {
            stravaManager.login(call, it)
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

  private suspend fun ApplicationCall.respondPlaceholderStravaPage() {
    val pageName = request.path().substringAfter("/strava/").let { it[0].toUpperCase() + it.substring(1) }
    respond(PebbleContent("placeholder.peb", mapOf("pageName" to pageName)))
  }
}
