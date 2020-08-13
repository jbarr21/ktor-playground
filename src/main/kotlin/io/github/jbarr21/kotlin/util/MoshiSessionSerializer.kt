package io.github.jbarr21.kotlin.util

import com.squareup.moshi.Moshi
import io.ktor.sessions.SessionSerializer

class MoshiSessionSerializer<T>(
  private val moshi: Moshi,
  private val type: java.lang.reflect.Type,
  configure: Moshi.() -> Unit = {}
) : SessionSerializer<T> {
  init {
    configure(moshi)
  }

  override fun serialize(session: T): String = moshi.adapter<T>(type).toJson(session)
  override fun deserialize(text: String): T = moshi.adapter<T>(type).fromJson(text)!!
}
