package io.github.jbarr21.kotlin

import dagger.Component
import io.github.jbarr21.kotlin.covid.CovidManager

@Component(
  modules = [ ApplicationModule::class ]
)
interface ApplicationComponent {
  fun covidManager(): CovidManager
}
