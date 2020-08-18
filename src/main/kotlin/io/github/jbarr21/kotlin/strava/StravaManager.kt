package io.github.jbarr21.kotlin.strava

import io.github.jbarr21.kotlin.strava.model.Activity
import javax.inject.Inject

class StravaManager @Inject constructor(
  private val stravaService: StravaService
) {

  suspend fun getActivities(accessToken: String): Int {
    val activities = mutableListOf<Activity>()
    var page = 1
    do {
      val resp = stravaService.getActivities(headerMap(accessToken), pageNum = page)
      resp.body()?.let { activities.addAll(it) }
      page++
    } while (resp.isSuccessful && resp.body()?.isNotEmpty() == true)
    return activities.size
  }

  private fun headerMap(accessToken: String) = mapOf(
    "Accept" to "application/json",
    "Authorization" to "Bearer $accessToken"
  )
}
