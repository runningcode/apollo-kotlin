package com.apollographql.apollo3.mpp


@JsFun("function millis() { return Date.now(); }")
external fun millis(): Double

@JsFun("function formattedDate() { return Date().toString(); }")
external fun formattedDate(): String

actual fun currentTimeMillis(): Long {
  return millis().toLong()
}

actual fun currentTimeFormatted(): String {
  return formattedDate()
}

actual fun currentThreadId(): String {
  return "wasm"
}

actual fun currentThreadName(): String {
  return currentThreadId()
}

actual fun platform() = Platform.Wasm
