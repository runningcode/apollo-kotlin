package com.apollographql.apollo3.mockserver

import com.apollographql.apollo3.annotations.ApolloDeprecatedSince
import com.apollographql.apollo3.annotations.ApolloExperimental
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.encodeUtf8
import okio.Closeable
import kotlin.js.JsName
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface MockServer : Closeable {
  /**
   * Returns the root url for this server
   *
   * It will suspend until a port is found to listen to
   */
  suspend fun url(): String

  @Deprecated("use close instead", ReplaceWith("close"), DeprecationLevel.ERROR)
  @ApolloDeprecatedSince(ApolloDeprecatedSince.Version.v4_0_0)
  suspend fun stop() = close()

  /**
   * Closes the server.
   *
   * The locally bound address is freed immediately
   * Active connections might stay alive after this call but will eventually terminate
   */
  override fun close()

  /**
   * Enqueue a response
   */
  fun enqueue(mockResponse: MockResponse)

  /**
   * Return a request from the recorded requests or throw if no request has been received
   *
   * @see [awaitRequest] and [awaitWebSocketRequest]
   */
  fun takeRequest(): MockRequest

  /**
   * Wait for a request and return it
   *
   * @see [awaitRequest] and [awaitWebSocketRequest]
   */
  suspend fun awaitAnyRequest(timeout: Duration = 1.seconds): MockRequestBase

  class Builder {
    private var handler: MockServerHandler? = null
    private var handlePings: Boolean? = null
    private var tcpServer: TcpServer? = null

    fun handler(handler: MockServerHandler) = apply {
      this.handler = handler
    }

    fun handlePings(handlePings: Boolean)  = apply {
      this.handlePings = handlePings
    }

    fun tcpServer(tcpServer: TcpServer) = apply {
      this.tcpServer = tcpServer
    }

    fun build(): MockServer {
      return MockServerImpl(
          handler ?: QueueMockServerHandler(),
          handlePings ?: true,
          tcpServer ?: TcpServer()
      )
    }
  }
}

internal class MockServerImpl(
    private val mockServerHandler: MockServerHandler,
    private val handlePings: Boolean,
    private val server: TcpServer,
) : MockServer {
  private val requests = Channel<MockRequestBase>(Channel.UNLIMITED)
  private val scope = CoroutineScope(SupervisorJob())

  init {
    server.listen(::onSocket)
  }

  private fun onSocket(socket: TcpSocket) {
    scope.launch {
      //println("Socket bound: ${url()}")
      try {
        handleRequests(mockServerHandler, socket) {
          requests.trySend(it)
        }
      } catch (e: Exception) {
        if (e is CancellationException) {
          // We got cancelled from closing the server
          throw e
        }

        println("handling request failed")
        // There was a network exception
        e.printStackTrace()
      } finally {
        socket.close()
      }
    }
  }

  private suspend fun handleRequests(handler: MockServerHandler, socket: TcpSocket, onRequest: (MockRequestBase) -> Unit) {
    val buffer = Buffer()
    val reader = object : Reader {
      override val buffer: Buffer
        get() = buffer

      override suspend fun fillBuffer() {
        val data = socket.receive()
        buffer.write(data)
      }
    }

    while (true) {
      val request = readRequest(reader)
      onRequest(request)

      val response = handler.handle(request)

      delay(response.delayMillis)

      coroutineScope {
        if (request is WebsocketMockRequest) {
          launch {
            readFrames(reader) { message ->
              when {
                handlePings && message is PingFrame -> {
                  socket.send(pongFrame())
                }

                handlePings && message is PongFrame -> {
                  // do nothing
                }

                else -> {
                  request.messages.trySend(message)
                }
              }
            }
          }
        }
        writeResponse(response, request.version) {
          socket.send(it)
        }
      }
    }
  }

  override suspend fun url(): String {
    return server.address().let {
      // XXX: IPv6
      "http://127.0.0.1:${it.port}/"
    }
  }

  override fun close() {
    scope.cancel()
    server.close()
  }

  override fun enqueue(mockResponse: MockResponse) {
    (mockServerHandler as? QueueMockServerHandler)?.enqueue(mockResponse)
        ?: error("Apollo: cannot call MockServer.enqueue() with a custom handler")
  }

  override fun takeRequest(): MockRequest {
    val result = requests.tryReceive()

    return result.getOrThrow() as MockRequest
  }

  override suspend fun awaitAnyRequest(timeout: Duration): MockRequestBase {
    return withTimeout(timeout) {
      requests.receive()
    }
  }
}

@JsName("createMockServer")
fun MockServer(): MockServer = MockServerImpl(QueueMockServerHandler(), true, TcpServer())

@Deprecated("Use enqueueString instead", ReplaceWith("enqueueString"), DeprecationLevel.ERROR)
@ApolloDeprecatedSince(ApolloDeprecatedSince.Version.v4_0_0)
fun MockServer.enqueue(string: String = "", delayMs: Long = 0, statusCode: Int = 200) = enqueueString(string, delayMs, statusCode)

fun MockServer.enqueueString(string: String = "", delayMs: Long = 0, statusCode: Int = 200) {
  enqueue(MockResponse.Builder()
      .statusCode(statusCode)
      .body(string)
      .delayMillis(delayMs)
      .build())
}

@ApolloExperimental
interface MultipartBody {
  fun enqueuePart(bytes: ByteString, isLast: Boolean)
  fun enqueueDelay(delayMillis: Long)
}

@ApolloExperimental
fun MultipartBody.enqueueStrings(parts: List<String>, responseDelayMillis: Long = 0, chunksDelayMillis: Long = 0) {
  enqueueDelay(responseDelayMillis)
  parts.withIndex().forEach { (index, value) ->
    enqueueDelay(chunksDelayMillis)
    enqueuePart(value.encodeUtf8(), index == parts.lastIndex)
  }
}

@ApolloExperimental
fun MockServer.enqueueMultipart(
    partsContentType: String,
    headers: Map<String, String> = emptyMap(),
    boundary: String = "-",
): MultipartBody {
  val multipartBody = MultipartBodyImpl(boundary, partsContentType)
  enqueue(
      MockResponse.Builder()
          .body(multipartBody.consumeAsFlow())
          .headers(headers)
          .addHeader("Content-Type", """multipart/mixed; boundary="$boundary""")
          .addHeader("Transfer-Encoding", "chunked")
          .build()
  )

  return multipartBody
}

@ApolloExperimental
interface WebSocketBody {
  fun enqueueMessage(message: WebSocketMessage)
}

@ApolloExperimental
fun MockServer.enqueueWebSocket(
    headers: Map<String, String> = emptyMap(),
): WebSocketBody {
  val webSocketBody = WebSocketBodyImpl()
  enqueue(
      MockResponse.Builder()
          .statusCode(101)
          .body(webSocketBody.consumeAsFlow())
          .headers(headers)
          .addHeader("Upgrade", "websocket")
          .addHeader("Connection", "upgrade")
          .addHeader("Sec-WebSocket-Accept", "APOLLO_REPLACE_ME")
          .addHeader("Sec-WebSocket-Protocol", "APOLLO_REPLACE_ME")
          .build()
  )

  return webSocketBody
}

@ApolloExperimental
suspend fun MockServer.awaitWebSocketRequest(timeout: Duration = 1.seconds): WebsocketMockRequest {
  return awaitAnyRequest(timeout) as WebsocketMockRequest
}

suspend fun MockServer.awaitRequest(timeout: Duration = 1.seconds): MockRequest {
  return awaitAnyRequest(timeout) as MockRequest
}