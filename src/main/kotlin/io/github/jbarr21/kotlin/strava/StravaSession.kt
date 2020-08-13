package io.github.jbarr21.kotlin.strava

data class StravaSession(
  val accessToken: String,
  val athlete: Athlete
)
