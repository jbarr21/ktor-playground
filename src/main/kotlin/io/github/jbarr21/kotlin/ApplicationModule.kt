package io.github.jbarr21.kotlin

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import io.github.jbarr21.kotlin.covid.CovidManager
import io.github.jbarr21.kotlin.covid.CovidService
import retrofit2.Retrofit

@Module
object ApplicationModule {

  @JvmStatic
  @Provides
  fun moshi(): Moshi {
    return Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .build()
  }
}
