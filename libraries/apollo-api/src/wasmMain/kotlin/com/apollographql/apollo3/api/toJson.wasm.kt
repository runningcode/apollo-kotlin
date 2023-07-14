package com.apollographql.apollo3.api

import com.apollographql.apollo3.api.json.JsonWriter

/**
 *
 */
actual fun Operation.Data.toJson(
    jsonWriter: JsonWriter,
    customScalarAdapters: CustomScalarAdapters,
) {
  kotlinx.browser.document
  TODO("toJson is not supported on wasm")
}