package com.apollographql.apollo3.mockserver

internal actual class QueueMockServerHandler actual constructor() : MockServerHandler {
  actual fun enqueue(response: MockResponse) {
    TODO("Not yet implemented")
  }

  actual override fun handle(request: MockRequest): MockResponse {
    TODO("Not yet implemented")
  }
}