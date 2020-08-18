package io.github.jbarr21.kotlin.covid

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
object CovidModule {

  @JvmStatic
  @Provides
  fun covidService(): CovidService {
    return Retrofit.Builder()
      .baseUrl(CovidManager.COVID_REPORT_URL)
      .addConverterFactory(MoshiConverterFactory.create())
      .build()
      .create(CovidService::class.java)
  }
}
