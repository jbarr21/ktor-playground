package io.github.jbarr21.kotlin.strava

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Athlete(
  val badge_type_id: Long,
  val city: String?,
  val country: String?,
  val created_at: String,
  val firstname: String?,
  val follower: Any?,
  val friend: Any?,
  val id: Long,
  val lastname: String?,
  val premium: Boolean,
  val profile: String?,
  val profile_medium: String?,
  val resource_state: Long,
  val sex: String?,
  val state: String?,
  val summit: Boolean,
  val updated_at: String,
  val username: String?
) {
  val name = "${firstname.orEmpty()} ${lastname.orEmpty()}".trim()
}
