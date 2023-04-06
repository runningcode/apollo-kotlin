@file:JvmName("JavaOptionalAdapters")

package com.apollographql.apollo3.api.java.adapter

import com.apollographql.apollo3.api.Adapter
import com.apollographql.apollo3.api.AnyAdapter
import com.apollographql.apollo3.api.BooleanAdapter
import com.apollographql.apollo3.api.ScalarAdapters
import com.apollographql.apollo3.api.DoubleAdapter
import com.apollographql.apollo3.api.IntAdapter
import com.apollographql.apollo3.api.StringAdapter
import com.apollographql.apollo3.api.json.JsonReader
import com.apollographql.apollo3.api.json.JsonWriter
import java.util.Optional

/**
 * An adapter for Java's [Optional]. `null` is deserialized as [Optional.empty].
 */
class JavaOptionalAdapter<T : Any>(private val wrappedAdapter: Adapter<T>) : Adapter<Optional<T>> {
  override fun fromJson(reader: JsonReader, scalarAdapters: ScalarAdapters): Optional<T> {
    return if (reader.peek() == JsonReader.Token.NULL) {
      reader.skipValue()
      Optional.empty()
    } else {
      Optional.of(wrappedAdapter.fromJson(reader, scalarAdapters))
    }
  }

  override fun toJson(writer: JsonWriter, scalarAdapters: ScalarAdapters, value: Optional<T>) {
    if (!value.isPresent) {
      writer.nullValue()
    } else {
      wrappedAdapter.toJson(writer, scalarAdapters, value.get())
    }
  }
}

/*
 * Global instances of optional adapters for built-in scalar types
 */
@JvmField
val JavaOptionalStringAdapter = JavaOptionalAdapter(StringAdapter)

@JvmField
val JavaOptionalDoubleAdapter = JavaOptionalAdapter(DoubleAdapter)

@JvmField
val JavaOptionalIntAdapter = JavaOptionalAdapter(IntAdapter)

@JvmField
val JavaOptionalBooleanAdapter = JavaOptionalAdapter(BooleanAdapter)

@JvmField
val JavaOptionalAnyAdapter = JavaOptionalAdapter(AnyAdapter)
