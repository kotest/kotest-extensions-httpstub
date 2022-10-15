package io.kotest.extensions.httpstub

import com.github.tomakehurst.wiremock.client.WireMock

fun HttpResponse.toReturnBuilder() = WireMock.status(this.code.value)
