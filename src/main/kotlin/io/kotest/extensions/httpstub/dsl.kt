package io.kotest.extensions.httpstub

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode

const val DEFAULT_HOST = "0.0.0.0"

fun mappings(configure: HttpStubber.() -> Unit): HttpStubber.() -> Unit {
   return configure
}

class HttpStubber(private val server: WireMockServer) {

   fun post(url: String, fn: () -> HttpResponse) {
      server.stubFor(
         WireMock.post(WireMock.urlEqualTo(url)).willReturn(
            fn().toReturnBuilder()
         )
      )
   }

   fun get(url: String, fn: () -> HttpResponse) {
      server.stubFor(
         WireMock.get(WireMock.urlEqualTo(url)).willReturn(
            fn().toReturnBuilder()
         )
      )
   }

   fun patch(url: String, fn: () -> HttpResponse) {
      server.stubFor(
         WireMock.patch(WireMock.urlEqualTo(url)).willReturn(
            fn().toReturnBuilder()
         )
      )
   }

   fun put(url: String, fn: () -> HttpResponse) {
      server.stubFor(
         WireMock.put(WireMock.urlEqualTo(url)).willReturn(
            fn().toReturnBuilder()
         )
      )
   }

   fun delete(url: String, fn: () -> HttpResponse) {
      server.stubFor(
         WireMock.delete(WireMock.urlEqualTo(url)).willReturn(
            fn().toReturnBuilder()
         )
      )
   }

   /**
    * Adds a callback for responses to this pipeline.
    */
   fun listener(fn: (HttpRequest) -> Unit) {
      server.addMockServiceRequestListener { request, _ ->
         fn(request.toHttpRequest())
      }
   }

   fun okJson(body: String): HttpResponse =
      HttpResponse(HttpStatusCode.OK, body).withContentType(ContentType.Application.Json)

   fun ok(): HttpResponse = HttpResponse(HttpStatusCode.OK)

   fun okTextPlain(body: String): HttpResponse =
      HttpResponse(HttpStatusCode.OK, body).withContentType(ContentType.Text.Plain)
}

data class HttpRequest(
   val url: String,
   val absoluteUrl: String,
   val headers: Map<String, List<String>>,
   val body: ByteArray,
)

data class HttpResponse(
   val code: HttpStatusCode,
   val body: String? = null,
   val headers: Map<String, String> = emptyMap()
)

fun HttpResponse.withHeader(name: String, value: String) = copy(headers = headers + (name to value))

fun HttpResponse.withContentType(contentType: ContentType) =
   copy(headers = headers + (HttpHeaders.ContentType to contentType.toString()))
