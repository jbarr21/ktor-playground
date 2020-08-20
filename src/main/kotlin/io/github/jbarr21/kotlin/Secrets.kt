package io.github.jbarr21.kotlin

object Secrets {
  val DATABASE_URL = System.getenv("DATABASE_URL")
  val STRAVA_CLIENT_ID = System.getenv("STRAVA_CLIENT_ID")
  val STRAVA_CLIENT_SECRET = System.getenv("STRAVA_CLIENT_SECRET")
  val STRAVA_SESSION_SIGN_KEY = System.getenv("STRAVA_SESSION_SIGN_KEY")
}