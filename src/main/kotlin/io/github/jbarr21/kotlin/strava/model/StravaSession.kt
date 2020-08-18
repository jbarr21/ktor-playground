package io.github.jbarr21.kotlin.strava.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StravaSession(
  val accessToken: String,
  val athlete: Athlete
)
