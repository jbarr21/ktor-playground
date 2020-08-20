package io.github.jbarr21.kotlin.strava.prop

data class MainProps(
  val name: String,
  val profileImage: String,
  val activities: List<ActivityProps>
)
