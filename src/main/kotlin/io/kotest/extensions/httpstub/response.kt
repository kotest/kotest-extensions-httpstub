package io.kotest.extensions.httpstub

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode

fun HttpResponse.toReturnBuilder(): ResponseDefinitionBuilder? {
   val resp = WireMock.status(this.code.value)
   this.headers.forEach { (name, value) -> resp.withHeader(name, value) }
   if (this.body != null) resp.withBody(this.body)
   return resp
}

data class HttpResponse(
   val code: HttpStatusCode,
   val body: String? = null,
   val headers: Map<String, String> = emptyMap()
)

fun HttpResponse.withHeader(name: String, value: String) = copy(headers = headers + (name to value))

fun HttpResponse.withContentType(contentType: ContentType) =
   copy(headers = headers + (HttpHeaders.ContentType to contentType.toString()))
