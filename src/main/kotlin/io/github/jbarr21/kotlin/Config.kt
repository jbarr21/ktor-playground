package io.github.jbarr21.kotlin

object Config {
  const val USE_FAKE_STRAVA_DATA = false

  val LOGGING_ENABLED = System.getenv()["LOGGING_ENABLED"].orEmpty().isNotEmpty()
}
