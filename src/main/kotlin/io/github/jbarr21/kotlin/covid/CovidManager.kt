package io.github.jbarr21.kotlin.covid

import com.squareup.moshi.Moshi
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CovidManager @Inject constructor(
  private val covidService: CovidService,
  private val moshi: Moshi
) {

  suspend fun getNewCovidCases(): CovidResponse {
    val isLatestDateToday = covidService.getCasesCsvExistence(Instant.now().asString()).isSuccessful
    val latestDate = Instant.now().let { if (isLatestDateToday) it else it.minus(Duration.ofDays(1)) }
    val ydayDate = latestDate.minus(Duration.ofDays(1))
    val newConfirmedCases = covidConfirmedCases(latestDate) - covidConfirmedCases(ydayDate)
    return CovidResponse(
      cases = newConfirmedCases,
      country = "US",
      date = latestDate.asString()
    )
  }

  private suspend fun covidConfirmedCases(date: Instant): Long {
    val csvLatest = covidService.getCasesCsv(date.asString()).body()?.string().orEmpty()
    return csvLatest.substringAfter("\n")
      .split("\n")
      .filter { it.isNotEmpty() }
      .foldRight(0L) { line, acc ->
        acc + line.split(",")[5].let { if (it.isNotEmpty()) it.toInt() else 0 }
      }
  }

  private fun Instant.asString() = dateFormatter.format(this)

  companion object {
    const val COVID_REPORT_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/"

    private val dateFormatter = DateTimeFormatter.ofPattern("MM-dd-YYYY").withZone(ZoneId.of("UTC"))
  }
}