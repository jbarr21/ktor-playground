package io.github.jbarr21.kotlin.strava.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Stats(
  val count: Long,
  val distance: Double,
  val moving_time: Long,
  val elapsed_time: Long,
  val elevation_gain: Double,
  val achievement_count: Long
)
