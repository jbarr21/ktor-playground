package io.github.jbarr21.kotlin

import com.moczul.ok2curl.CurlInterceptor
import com.moczul.ok2curl.logger.Loggable
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.exposed.sql.Database
import javax.inject.Singleton

@Module
object AppModule {

  @JvmStatic
  @Singleton
  @Provides
  fun database(): Database {
    val userPassPair: Pair<String, String> = Secrets.DATABASE_URL
      .substringAfter("://")
      .substringBefore("@")
      .split(":")
      .let { it[0] to it[1] }
    return Database.connect(
      "jdbc:postgresql://${Secrets.DATABASE_URL.substringAfter("@")}",
      driver = "org.postgresql.Driver",
      user = userPassPair.first,
      password = userPassPair.second)
  }

  @JvmStatic
  @Singleton
  @Provides
  fun moshi(): Moshi {
    return Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .build()
  }

  @JvmStatic
  @Singleton
  @Provides
  fun okHttpClient(): OkHttpClient {
    return OkHttpClient.Builder().apply {
        if (Config.LOGGING_ENABLED) {
          addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) = println("[OkHttp] $message")
          }).setLevel(HttpLoggingInterceptor.Level.HEADERS))
          addInterceptor(CurlInterceptor { println("[Ok2Curl] $it") })
        }
      }
      .build()
  }
}
