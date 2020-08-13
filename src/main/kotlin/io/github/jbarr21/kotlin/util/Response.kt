package io.github.jbarr21.kotlin.util

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Response(val status: String)
