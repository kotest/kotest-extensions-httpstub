package io.kotest.extensions.httpstub

import com.github.tomakehurst.wiremock.client.VerificationException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.get
import io.ktor.client.request.post

class CheckForUnmatchedTest : FunSpec() {
   init {

      val client = HttpClient(Apache)
      val server = install(HttpStub) {
         resetMappings = Reset.TEST
         resetRequests = Reset.TEST
      }

      test("shouldNotHaveUnmatchedRequests should throw with unmatched") {
         server.mappings {
            get("/bar") { ok() }
         }
         client.post("http://localhost:${server.port}/qweqe")
         shouldThrow<VerificationException> {
            server.shouldNotHaveUnmatchedRequests()
         }
      }

      test("shouldNotHaveUnmatchedRequests should not throw if no unmatched") {
         server.mappings {
            get("/bar") { ok() }
         }
         client.get("http://localhost:${server.port}/bar")
         server.shouldNotHaveUnmatchedRequests()
      }
   }
}
