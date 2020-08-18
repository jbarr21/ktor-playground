package io.github.jbarr21.kotlin.strava.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Map(
  val id: String,
  val summary_polyline: String?,
  val resource_state: Int
)
