package io.github.jbarr21.kotlin.strava.prop

import io.github.jbarr21.kotlin.strava.model.Athlete
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

data class ActivityProps(
  val id: Long,
  val athleteId: Long,
  val timestampUtc: Long,
  val name: String,
  val distance: Double,
  val elevation: Double,
  val speed: Double,
  val power: Double
) {
  val startTime = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
    .withZone(ZoneId.systemDefault())
    .format(Instant.ofEpochSecond(timestampUtc))
}
