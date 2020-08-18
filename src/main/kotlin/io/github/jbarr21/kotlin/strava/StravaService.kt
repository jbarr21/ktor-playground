package io.github.jbarr21.kotlin.strava

import io.github.jbarr21.kotlin.strava.model.Activity
import io.github.jbarr21.kotlin.strava.model.Athlete
import io.github.jbarr21.kotlin.strava.model.AthleteStats
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface StravaService {
  @GET("athlete")
  suspend fun getAthelete(@HeaderMap headers: Map<String, String>): Response<Athlete>

  @GET("athletes/{athleteId}/stats")
  suspend fun getAtheleteStats(
    @HeaderMap headers: Map<String, String>,
    @Path("athleteId") athleteId: Long
  ): Response<AthleteStats>

  @GET("athlete/activities")
  suspend fun getActivities(
    @HeaderMap headers: Map<String, String>,
    @Query("per_page") perPage: Int = 100,
    @Query("page") pageNum: Int = 1
  ): Response<List<Activity>>

  @GET("activities/{activityId}")
  suspend fun getActivity(
    @HeaderMap headers: Map<String, String>,
    @Path("activityId") activityId: Long
  ): Response<Activity>
}
