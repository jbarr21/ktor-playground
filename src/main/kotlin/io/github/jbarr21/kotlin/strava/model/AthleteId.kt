package io.github.jbarr21.kotlin.strava.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AthleteId(
  val id: Long,
  val resource_state: Int
)
