package io.github.jbarr21.kotlin.strava

import dagger.Module
import dagger.Provides
import io.github.jbarr21.kotlin.Secrets
import io.ktor.auth.OAuthServerSettings
import io.ktor.http.HttpMethod

@Module
object StravaModule {

  @JvmStatic
  @Provides
  fun stravaOAuth2ServerSettings(): OAuthServerSettings.OAuth2ServerSettings {
    return OAuthServerSettings.OAuth2ServerSettings(
      name = "strava",
      authorizeUrl = "https://www.strava.com/oauth/authorize",
      accessTokenUrl = "https://www.strava.com/oauth/token",
      requestMethod = HttpMethod.Post,

      clientId = Secrets.STRAVA_CLIENT_ID,
      clientSecret = Secrets.STRAVA_CLIENT_SECRET,
      defaultScopes = listOf("read")
    )
  }
}
