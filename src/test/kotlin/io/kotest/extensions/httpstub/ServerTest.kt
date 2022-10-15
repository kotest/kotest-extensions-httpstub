package io.kotest.extensions.httpstub

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode

class ServerTest : FunSpec() {
   init {

      val client = HttpClient(Apache)

      val server = install(HttpStub) {
         port = 24453
         resetRequests = Reset.TEST
      }

      server.mappings(reuse(1, 2))

      test("support fixed port") {
         server.mappings {
            post("/foo") {
               HttpResponse(HttpStatusCode.OK, "hello")
            }
         }
         val resp = client.post("http://localhost:24453/foo")
         resp.status shouldBe HttpStatusCode.OK
      }

      test("server should list all invoked endpoints") {
         client.post("http://localhost:24453/foo")
         client.delete("http://localhost:24453/bar")
         server.invokedEndpoints() shouldBe listOf("/foo", "/bar")
      }
   }
}

fun reuse(source: Long, target: Long) = mappings {
   post("/internal/v1/$source/$target") {
      okJson("{}")
   }
   get("/internal/v2/bar") {
      okJson("{}")
   }
}
