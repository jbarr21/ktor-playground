package io.github.jbarr21.kotlin

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

@Module
object ApplicationModule {

  @JvmStatic
  @Provides
  fun moshi(): Moshi {
    return Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .build()
  }

  @JvmStatic
  @Provides
  fun okHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
//      .addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
//        override fun log(message: String) = println("[OkHttp] $message")
//      }).setLevel(HttpLoggingInterceptor.Level.BODY))
//      .addInterceptor(CurlInterceptor(Loggable { println("[Ok2Curl] $it") }))
      .build()
  }
}
