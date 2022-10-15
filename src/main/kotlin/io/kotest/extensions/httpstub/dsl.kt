package io.kotest.extensions.httpstub

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlin.random.Random

fun httpstub(configure: HttpStub.() -> Unit): Server {
   val port = Random.nextInt(10000, 65000)
   val server = WireMockServer(WireMockConfiguration.wireMockConfig().port(port))
   server.start()
   HttpStub(server).configure()
   return Server(server)
}

data class Server(val server: WireMockServer) {

   val port = server.port()
   val baseUrl: String = server.baseUrl()
   val isHttp = server.isHttpEnabled
   val isHttps = server.isHttpsEnabled

   private val invokedEndpoints = mutableListOf<String>()

   init {
      server.addMockServiceRequestListener { request, _ ->
         invokedEndpoints.add(request.url)
      }
   }

   /**
    * Returns a list of the endpoints invoked, eg "/foo", "/bar"
    */
   fun invokedEndpoints(): List<String> = invokedEndpoints
}

class HttpStub(private val server: WireMockServer) {

   fun post(url: String, response: (HttpRequest) -> HttpResponse) {
      server.stubFor(
         WireMock.post(WireMock.urlEqualTo(url)).willReturn(
            response(HttpRequest(url)).toReturnBuilder()
         )
      )
   }

   fun get(url: String, response: (HttpRequest) -> HttpResponse) {
      server.stubFor(
         WireMock.get(WireMock.urlEqualTo(url)).willReturn(
            response(HttpRequest(url)).toReturnBuilder()
         )
      )
   }

   fun patch(url: String, response: (HttpRequest) -> HttpResponse) {
      server.stubFor(
         WireMock.patch(WireMock.urlEqualTo(url)).willReturn(
            response(HttpRequest(url)).toReturnBuilder()
         )
      )
   }

   fun put(url: String, response: (HttpRequest) -> HttpResponse) {
      server.stubFor(
         WireMock.put(WireMock.urlEqualTo(url)).willReturn(
            response(HttpRequest(url)).toReturnBuilder()
         )
      )
   }

   fun delete(url: String, response: (HttpRequest) -> HttpResponse) {
      server.stubFor(
         WireMock.delete(WireMock.urlEqualTo(url)).willReturn(
            response(HttpRequest(url)).toReturnBuilder()
         )
      )
   }

   /**
    * Adds a callback for responses to this pipeline.
    */
   fun listener(fn: (HttpRequest) -> Unit) {
      server.addMockServiceRequestListener { request, _ -> fn(HttpRequest(request.url)) }
   }
}

fun json(body: String): HttpResponse = HttpResponse(HttpStatusCode.OK, body)

data class HttpRequest(val url: String)

data class HttpResponse(
   val code: HttpStatusCode,
   val body: String? = null,
   val headers: Map<String, String> = emptyMap()
)

fun HttpResponse.withHeader(name: String, value: String) = copy(headers = headers + (name to value))

fun HttpResponse.withContentType(contentType: ContentType) =
   copy(headers = headers + (HttpHeaders.ContentType to contentType.toString()))
