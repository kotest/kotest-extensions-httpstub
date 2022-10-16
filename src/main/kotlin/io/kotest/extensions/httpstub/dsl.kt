package io.kotest.extensions.httpstub

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode

const val DEFAULT_HOST = "0.0.0.0"

fun mappings(configure: HttpStubber.() -> Unit): HttpStubber.() -> Unit {
   return configure
}

class RequestStubber {

   val headers = mutableMapOf<String, String>()

   fun header(name: String, value: String) {
      headers[name] = value
   }
}

class HttpStubber(private val server: WireMockServer) {

   private fun stub(builder: MappingBuilder, fn: RequestStubber.() -> HttpResponse) {
      val rs = RequestStubber()
      val resp = rs.fn()
      rs.headers.forEach { (name, value) -> builder.withHeader(name, EqualToPattern(value)) }
      server.stubFor(builder.willReturn(resp.toReturnBuilder()))
   }

   fun get(url: Regex, fn: RequestStubber.() -> HttpResponse) {
      val builder = WireMock.get(WireMock.urlMatching(url.pattern))
      stub(builder, fn)
   }

   fun get(url: String, fn: RequestStubber.() -> HttpResponse) {
      val builder = WireMock.get(WireMock.urlEqualTo(url))
      stub(builder, fn)
   }

   fun post(url: Regex, fn: RequestStubber.() -> HttpResponse) {
      val builder = WireMock.post(WireMock.urlMatching(url.pattern))
      stub(builder, fn)
   }

   fun post(url: String, fn: RequestStubber.() -> HttpResponse) {
      val builder = WireMock.post(WireMock.urlEqualTo(url))
      stub(builder, fn)
   }

   fun patch(url: Regex, fn: RequestStubber.() -> HttpResponse) {
      val builder = WireMock.patch(WireMock.urlMatching(url.pattern))
      stub(builder, fn)
   }

   fun patch(url: String, fn: RequestStubber.() -> HttpResponse) {
      val builder = WireMock.patch(WireMock.urlEqualTo(url))
      stub(builder, fn)
   }

   fun head(url: Regex, fn: RequestStubber.() -> HttpResponse) {
      val builder = WireMock.head(WireMock.urlMatching(url.pattern))
      stub(builder, fn)
   }

   fun head(url: String, fn: RequestStubber.() -> HttpResponse) {
      val builder = WireMock.head(WireMock.urlEqualTo(url))
      stub(builder, fn)
   }

   fun options(url: Regex, fn: RequestStubber.() -> HttpResponse) {
      val builder = WireMock.options(WireMock.urlMatching(url.pattern))
      stub(builder, fn)
   }

   fun options(url: String, fn: RequestStubber.() -> HttpResponse) {
      val builder = WireMock.options(WireMock.urlEqualTo(url))
      stub(builder, fn)
   }

   fun put(url: Regex, fn: RequestStubber.() -> HttpResponse) {
      val builder = WireMock.put(WireMock.urlMatching(url.pattern))
      stub(builder, fn)
   }

   fun put(url: String, fn: RequestStubber.() -> HttpResponse) {
      val builder = WireMock.put(WireMock.urlEqualTo(url))
      stub(builder, fn)
   }

   fun delete(url: Regex, fn: RequestStubber.() -> HttpResponse) {
      val builder = WireMock.delete(WireMock.urlMatching(url.pattern))
      stub(builder, fn)
   }

   fun delete(url: String, fn: RequestStubber.() -> HttpResponse) {
      val builder = WireMock.delete(WireMock.urlEqualTo(url))
      stub(builder, fn)
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

   fun internalServerError() = HttpResponse(HttpStatusCode.InternalServerError)

   fun badRequest() = HttpResponse(HttpStatusCode.BadRequest)
}
