package io.github.jbarr21.kotlin

import com.squareup.moshi.Moshi
import dagger.Component
import io.github.jbarr21.kotlin.covid.CovidManager
import io.github.jbarr21.kotlin.covid.CovidModule
import io.github.jbarr21.kotlin.strava.StravaManager
import io.github.jbarr21.kotlin.strava.StravaModule
import io.ktor.auth.OAuthServerSettings

@Component(
  modules = [
    ApplicationModule::class,
    CovidModule::class,
    StravaModule::class
  ]
)
interface ApplicationComponent {
  fun covidManager(): CovidManager

  fun moshi(): Moshi

  fun stravaManager(): StravaManager

  fun stravaOAuth2ServerSettings(): OAuthServerSettings.OAuth2ServerSettings
}
