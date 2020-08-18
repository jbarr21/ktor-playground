package io.github.jbarr21.kotlin.strava.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AthleteStats(
  val biggest_ride_distance: Double,
  val biggest_climb_elevation_gain: Double,
  val recent_ride_totals: Stats,
  val recent_run_totals: Stats,
  val recent_swim_totals: Stats,
  val ytd_ride_totals: Stats,
  val ytd_run_totals: Stats,
  val ytd_swim_totals: Stats,
  val all_ride_totals: Stats,
  val all_run_totals: Stats,
  val all_swim_totals: Stats
)
