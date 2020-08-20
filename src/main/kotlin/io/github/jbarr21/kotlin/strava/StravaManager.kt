package io.github.jbarr21.kotlin.strava

import com.squareup.moshi.Moshi
import fetchFromDb
import insertIntoDb
import io.github.jbarr21.kotlin.Config
import io.github.jbarr21.kotlin.strava.db.Activities
import io.github.jbarr21.kotlin.strava.model.*
import io.github.jbarr21.kotlin.strava.prop.ActivityProps
import io.github.jbarr21.kotlin.strava.prop.MainProps
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.pebble.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import toProp
import javax.inject.Inject

class StravaManager @Inject constructor(
  private val database: Database,
  private val moshi: Moshi,
  private val stravaService: StravaService
) {

  fun login(call: ApplicationCall, principal: OAuthAccessTokenResponse.OAuth2) {
    moshi
      .adapter(Athlete::class.java)
      .fromJson(principal.extraParameters["athlete"].orEmpty())
      ?.let {
        call.sessions.set(StravaSession(principal.accessToken, it))
      }
  }

  suspend fun mainPage(call: ApplicationCall, session: StravaSession) = with (call) {
    val activityProps = if (Config.USE_FAKE_STRAVA_DATA) {
      FAKE_ACTIVITIES
    } else {
      val athleteId = session.athlete.id
      val activities = transaction {
        if (Config.LOGGING_ENABLED) {
          addLogger(StdOutSqlLogger)
        }
        SchemaUtils.create(Activities)
        Activities.fetchFromDb(session.athlete.id)
      }
      fetchNewActivities(athleteId, session.accessToken)
      activities
    }

    val props = MainProps(session.athlete.name, session.athlete.profile.orEmpty(), activityProps)
    respond(PebbleContent("main.peb", mapOf("props" to props)))
  }

  private suspend fun fetchNewActivities(athleteId: Long, accessToken: String) {
    val maxTimestamp = transaction<Long> {
      Activities.slice(Activities.timestamp.max())
        .select { Activities.athleteId eq athleteId }
        .firstOrNull()
        ?.getOrNull(Activities.timestamp.max())
        ?: 0L
    }

    fetchActivitiesFromApi(accessToken, after = maxTimestamp)
      .map { it.toProp() }
      .sortedByDescending { it.timestampUtc }
      .also {
        transaction {
          it.forEach { it.insertIntoDb() }
        }
      }
  }

  private suspend fun fetchActivitiesFromApi(accessToken: String, after: Long = 0): List<Activity> {
    val activities = mutableListOf<Activity>()
    var page = 1
    do {
      val resp = stravaService.getActivities(headerMap(accessToken), pageNum = page, afterTimestamp = after)
      resp.body()?.let { activities.addAll(it) }
      page++
    } while (resp.isSuccessful && resp.body()?.isNotEmpty() == true)
    return activities
  }

  private fun headerMap(accessToken: String) = mapOf(
    "Accept" to "application/json",
    "Authorization" to "Bearer $accessToken"
  )

  companion object {
    const val STRAVA_OAUTH = "strava-oauth"
    private val FAKE_ACTIVITIES = listOf(
      ActivityProps(3, 5678905, 1597876396, "Point Richmond", 35.93, 571.0, 15.4, 103.0),
      ActivityProps(2, 5678905, 1586866295, "San Rafael", 50.31, 1076.0, 12.6, 73.0),
      ActivityProps(1, 5678905, 1575856194, "Bay Ridge Trail", 25.0, 279.0, 13.9, 79.0)
    )

    fun Route.redirectUnauthenticatedStravaRoutes() = intercept(ApplicationCallPipeline.Setup) {
      if (call.request.path().startsWith("/strava/")) {
        val subpath = call.request.path().substringAfter("/strava/").substringBefore("?")
        if (subpath !in setOf("login", "logout") && call.sessions.get<StravaSession>() == null) {
          call.respondRedirect("/strava")
          return@intercept finish()
        }
      }
    }
  }
}
