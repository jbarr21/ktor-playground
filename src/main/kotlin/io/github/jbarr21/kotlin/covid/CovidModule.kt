package io.github.jbarr21.kotlin.covid

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
object CovidModule {

  @JvmStatic
  @Provides
  fun covidService(): CovidService {
    return Retrofit.Builder()
      .baseUrl(CovidManager.COVID_REPORT_URL)
      .build()
      .create(CovidService::class.java)
  }
}
