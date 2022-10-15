package io.kotest.extensions.httpstub

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock

fun HttpResponse.toReturnBuilder(): ResponseDefinitionBuilder? {
   val resp = WireMock.status(this.code.value)
   this.headers.forEach { (name, value) -> resp.withHeader(name, value) }
   return resp
}
