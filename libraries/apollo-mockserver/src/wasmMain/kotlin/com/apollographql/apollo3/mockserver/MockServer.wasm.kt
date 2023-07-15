package com.apollographql.apollo3.mockserver

import com.apollographql.apollo3.annotations.ApolloExperimental

@ApolloExperimental
actual class MockServer actual constructor(mockServerHandler: MockServerHandler) : MockServerInterface {
  override suspend fun url(): String {
    TODO("Not yet implemented")
  }

  override suspend fun stop() {
    TODO("Not yet implemented")
  }

  override val mockServerHandler: MockServerHandler
    get() = TODO("Not yet implemented")

  override fun enqueue(mockResponse: MockResponse) {
    TODO("Not yet implemented")
  }

  override fun takeRequest(): MockRequest {
    TODO("Not yet implemented")
  }
}