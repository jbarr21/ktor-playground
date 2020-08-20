package io.github.jbarr21.kotlin.strava.model

typealias Miles = Double
typealias Meters = Double
typealias Feet = Double
typealias Watts = Double
typealias Seconds = Long
typealias Kilojoules = Double
typealias MetersPerSec = Double

fun Meters.toMiles() = this / 1609.34
fun Meters.toFeet() = this * 3.28084
fun MetersPerSec.toMph() = this * 2.23694