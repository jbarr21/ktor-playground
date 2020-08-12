package io.github.jbarr21.kotlin.covid

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CovidResponse(
  val cases: Long,
  val country: String,
  val date: String
)
