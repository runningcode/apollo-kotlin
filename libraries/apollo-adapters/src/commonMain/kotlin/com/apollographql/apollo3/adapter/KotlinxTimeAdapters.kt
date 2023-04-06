package com.apollographql.apollo3.adapter

import com.apollographql.apollo3.api.Adapter
import com.apollographql.apollo3.api.ScalarAdapters
import com.apollographql.apollo3.api.json.JsonReader
import com.apollographql.apollo3.api.json.JsonWriter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

/**
 * An [Adapter] that converts an ISO 8601 String like "2010-06-01T22:19:44.475Z" to/from
 * a [kotlinx.datetime.Instant]
 *
 * It requires Android Gradle plugin 4.0 or newer and [core library desugaring](https://developer.android.com/studio/write/java8-support#library-desugaring).
 */
object KotlinxInstantAdapter : Adapter<Instant> {
  override fun fromJson(reader: JsonReader, scalarAdapters: ScalarAdapters): Instant {
    return Instant.parse(reader.nextString()!!)
  }

  override fun toJson(writer: JsonWriter, scalarAdapters: ScalarAdapters, value: Instant) {
    writer.value(value.toString())
  }
}

/**
 * An [Adapter] that converts an ISO 8601 String without time zone information like "2010-06-01T22:19:44.475" to/from
 * a [kotlinx.datetime.LocalDateTime]
 *
 * It requires Android Gradle plugin 4.0 or newer and [core library desugaring](https://developer.android.com/studio/write/java8-support#library-desugaring).
 */
object KotlinxLocalDateTimeAdapter : Adapter<LocalDateTime> {
  override fun fromJson(reader: JsonReader, scalarAdapters: ScalarAdapters): LocalDateTime {
    return LocalDateTime.parse(reader.nextString()!!)
  }

  override fun toJson(writer: JsonWriter, scalarAdapters: ScalarAdapters, value: LocalDateTime) {
    writer.value(value.toString())
  }
}

/**
 * An [Adapter] that converts an ISO 8601 String like "2010-06-01" to/from
 * a [kotlinx.datetime.LocalDate]
 *
 * It requires Android Gradle plugin 4.0 or newer and [core library desugaring](https://developer.android.com/studio/write/java8-support#library-desugaring).
 */
object KotlinxLocalDateAdapter : Adapter<LocalDate> {
  override fun fromJson(reader: JsonReader, scalarAdapters: ScalarAdapters): LocalDate {
    return LocalDate.parse(reader.nextString()!!)
  }

  override fun toJson(writer: JsonWriter, scalarAdapters: ScalarAdapters, value: LocalDate) {
    writer.value(value.toString())
  }
}

/**
 * An [Adapter] that converts an ISO 8601 String like "14:35:00" to/from
 * a [kotlinx.datetime.LocalDate]
 *
 * It requires Android Gradle plugin 4.0 or newer and [core library desugaring](https://developer.android.com/studio/write/java8-support#library-desugaring).
 */
object KotlinxLocalTimeAdapter : Adapter<LocalTime> {
  override fun fromJson(reader: JsonReader, scalarAdapters: ScalarAdapters): LocalTime {
    return LocalTime.parse(reader.nextString()!!)
  }

  override fun toJson(writer: JsonWriter, scalarAdapters: ScalarAdapters, value: LocalTime) {
    writer.value(value.toString())
  }
}
