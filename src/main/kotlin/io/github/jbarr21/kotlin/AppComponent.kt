package io.github.jbarr21.kotlin

import dagger.BindsInstance
import dagger.Component
import io.github.jbarr21.kotlin.covid.CovidModule
import io.github.jbarr21.kotlin.strava.StravaModule
import io.ktor.application.Application
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    AppModule::class,
    CovidModule::class,
    StravaModule::class
  ]
)
interface AppComponent {
  fun app(): App

  @Component.Builder
  interface Builder {
    @BindsInstance
    fun application(application: Application): Builder

    fun build(): AppComponent
  }
}
