package io.github.jbarr21.kotlin

import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.isSuccess
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

const val COVID_REPORT_URL =
  "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports_us/{DATE}.csv"

val dateFormatter = DateTimeFormatter.ofPattern("MM-dd-YYYY").withZone(ZoneId.of("UTC"))
val client = HttpClient(OkHttp) {
  expectSuccess = false
  followRedirects = true
}

fun main(args: Array<String>) {
  val port = System.getenv().let { it["PORT"]?.toInt() ?: 8080 }
  val server = embeddedServer(Netty, port = port) {
    routing {
      get("/") {
        call.respondText("Hello World!", ContentType.Text.Plain)
      }
      get("/api") {
        call.respondText {
          val id = (Math.random() * 1000000).toInt()
          """
            {
              "userId": $id,
              "id": $id,
              "title": "delectus aut autem",
              "completed": ${Math.random() < 0.5}
            }
          """.trimIndent()
        }
      }
      get("/covid") {
        val isLatestDateToday = client
          .head<HttpResponse>(COVID_REPORT_URL.replace("{DATE}", dateFormatter.format(Instant.now())))
          .status.isSuccess()

        val latestDate = Instant.now().let { if (isLatestDateToday) it else it.minus(Duration.ofDays(1)) }
        val newCases = covidConfirmedCases(client, latestDate) - covidConfirmedCases(client, latestDate.minus(Duration.ofDays(1)))
        call.respondText("New COVID-19 cases in the US: ${NumberFormat.getInstance().format(newCases)}")
      }
      get("/hello") {
        val name = call.parameters["name"] ?: "World"
        call.respondText("Hello, $name!")
      }
    }
  }
  server.start(wait = true)
}

private suspend fun covidConfirmedCases(client: HttpClient, date: Instant): Long {
  val csvLatest = client.get<String>(COVID_REPORT_URL.replace("{DATE}", dateFormatter.format(date)))
  return csvLatest.substringAfter("\n")
    .split("\n")
    .filter { it.isNotEmpty() }
    .foldRight(0L) { line, acc ->
      acc + line.split(",")[5].let { if (it.isNotEmpty()) it.toInt() else 0 }
    }
}