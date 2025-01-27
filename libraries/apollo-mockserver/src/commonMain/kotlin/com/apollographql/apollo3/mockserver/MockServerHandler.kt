package com.apollographql.apollo3.mockserver

interface MockServerHandler {
  /**
   * Handles the given [MockRequest].
   *
   * This method is called from one or several background threads and must be thread-safe.
   */
  fun handle(request: MockRequest): MockResponse
}