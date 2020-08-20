package io.github.jbarr21.kotlin.strava

import dagger.Module
import dagger.Provides
import io.github.jbarr21.kotlin.Secrets
import io.ktor.auth.OAuthServerSettings
import io.ktor.http.HttpMethod
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
object StravaModule {

  @JvmStatic
  @Singleton
  @Provides
  fun stravaOAuth2ServerSettings(): OAuthServerSettings.OAuth2ServerSettings {
    return OAuthServerSettings.OAuth2ServerSettings(
      name = "strava",
      authorizeUrl = "https://www.strava.com/oauth/authorize",
      accessTokenUrl = "https://www.strava.com/oauth/token",
      requestMethod = HttpMethod.Post,

      clientId = Secrets.STRAVA_CLIENT_ID,
      clientSecret = Secrets.STRAVA_CLIENT_SECRET,
      defaultScopes = listOf("activity:read_all")
    )
  }

  @JvmStatic
  @Singleton
  @Provides
  fun serviceService(okHttpClient: OkHttpClient): StravaService {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl("https://www.strava.com/api/v3/")
      .addConverterFactory(MoshiConverterFactory.create())
      .build()
      .create(StravaService::class.java)
  }
}
