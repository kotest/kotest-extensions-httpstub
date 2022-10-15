package io.kotest.extensions.httpstub

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.ktor.http.HttpStatusCode
import kotlin.random.Random

fun httpstub(configure: HttpStub.() -> Unit): Server {
//   server.stubFor(
//      WireMock.post(WireMock.urlEqualTo("")).willReturn(
//         WireMock.ok().withHeader("Content-Type", "application/json").withBody(settings.toJson())
//      )
//   )

   val port = Random.nextInt(10000, 65000)
   val server = WireMockServer(WireMockConfiguration.wireMockConfig().port(port))
   server.start()

   val http = HttpStub(server).configure()
   return Server(server)
}

data class Server(val server: WireMockServer) {
   val port = server.port()
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
}

data class HttpRequest(val uri: String)
data class HttpResponse(val code: HttpStatusCode, val body: Any? = null)

