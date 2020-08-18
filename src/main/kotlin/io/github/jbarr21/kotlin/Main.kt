package io.github.jbarr21.kotlin

import com.ryanharter.ktor.moshi.moshi
import freemarker.cache.ClassTemplateLoader
import io.github.jbarr21.kotlin.strava.model.Athlete
import io.github.jbarr21.kotlin.strava.model.StravaSession
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
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
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
import org.slf4j.event.Level
import redirectUrl

private const val STRAVA_OAUTH = "strava-oauth"

private val applicationComponent by lazy { DaggerApplicationComponent.create() }

fun main(args: Array<String>) {
  val port = System.getenv()["PORT"]?.toInt() ?: 8080
  embeddedServer(
    Netty,
    watchPaths = listOf("build"),
    port = port,
    module = Application::mainModule
  ).start(wait = true)
}

fun Application.mainModule() {
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
  install(FreeMarker) {
    templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
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
  sessions.get<StravaSession>()?.let {
    val activityCount = 0 //applicationComponent.stravaManager().getActivities(it.accessToken)
    val props = MainProps(it.athlete.name, it.athlete.profile.orEmpty(), activityCount)
    respond(FreeMarkerContent("main.ftl", mapOf("props" to props)))
  } ?: respond(FreeMarkerContent("login.ftl", null))
}

data class MainProps(
  val name: String,
  val profileImage: String,
  val activityCount: Int,
  val activities: List<ActivityProps> = FAKE_ACTIVITIES
) {
  companion object {
    private val FAKE_ACTIVITIES = listOf(
      ActivityProps("Point Richmond", 35.93, 571.0, 15.4, 103),
      ActivityProps("San Rafael", 50.31, 1076.0, 12.6, 73),
      ActivityProps("Bay Ridge Trail", 25.0, 279.0, 13.9, 79)
    )
  }
}

data class ActivityProps(
  val name: String,
  val distance: Double,
  val elevation: Double,
  val speed: Double,
  val power: Int
)
