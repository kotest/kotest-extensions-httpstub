package io.kotest.extensions.httpstub

import com.github.tomakehurst.wiremock.http.Request

fun Request.toHttpRequest(): HttpRequest {
   return HttpRequest(
      url = this.url,
      headers = this.headers.keys().associateWith { this.header(it).values() }
   )
}
