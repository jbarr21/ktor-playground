import io.github.jbarr21.kotlin.strava.db.Activities
import io.github.jbarr21.kotlin.strava.model.Activity
import io.github.jbarr21.kotlin.strava.model.toFeet
import io.github.jbarr21.kotlin.strava.model.toMiles
import io.github.jbarr21.kotlin.strava.model.toMph
import io.github.jbarr21.kotlin.strava.prop.ActivityProps
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

// API to ViewModel
fun Activity.toProp() = ActivityProps(
  id,
  timestampUtc,
  name,
  distance.toMiles(),
  total_elevation_gain.toFeet(),
  average_speed.toMph(),
  average_watts ?: 0.0
)

// ViewModel to DB
fun ActivityProps.insertIntoDb() = this.let { activityProp ->
  Activities.insert {
    it[id] = activityProp.id
    it[timestamp] = activityProp.timestampUtc
    it[name] = activityProp.name
    it[distance] = activityProp.distance
    it[elevation] = activityProp.elevation
    it[speed] = activityProp.speed
    it[power] = activityProp.power
  }
}

// DB to ViewModel
fun Activities.fetchFromDb(): List<ActivityProps> {
  return Activities.selectAll()
    .orderBy(Activities.timestamp, SortOrder.DESC)
    .map {
      ActivityProps(
        it[id],
        it[timestamp],
        it[name],
        it[distance],
        it[elevation],
        it[speed],
        it[power] ?: 0.0
      )
    }
}
