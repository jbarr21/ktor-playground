package io.github.jbarr21.kotlin.strava.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Activities : Table() {
  val id: Column<Long> = long("id").uniqueIndex()
  val athleteId: Column<Long> = long("athleteId")
  val timestamp: Column<Long> = long("timestamp")
  val name: Column<String> = varchar("name", 200)
  val distance: Column<Double> = double("distance")
  val elevation: Column<Double> = double("elevation")
  val speed: Column<Double> = double("speed")
  val power: Column<Double?> = double("power").nullable()

  override val primaryKey = PrimaryKey(id)
}
