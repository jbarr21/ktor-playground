package io.github.jbarr21.kotlin.covid

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Path

interface CovidService {
  @HEAD("csse_covid_19_daily_reports_us/{date}.csv")
  suspend fun getCasesCsvExistence(@Path("date") date: String): Response<Void>

  @GET("csse_covid_19_daily_reports_us/{date}.csv")
  suspend fun getCasesCsv(@Path("date") date: String): Response<ResponseBody>
}
