package io.kotest.extensions.httpstub

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

      test("support fixed port") {
         val server = httpstub(port = 43523) {
            post("/foo") {
               HttpResponse(HttpStatusCode.ExpectationFailed, "hello")
            }
         }
         val resp = client.post("http://localhost:43523/foo")
         resp.status shouldBe HttpStatusCode.ExpectationFailed
      }

      test("server should list all invoked endpoints") {
         val server = httpstub {
            post("/foo") {
               HttpResponse(HttpStatusCode.OK, "hello")
            }
            delete("/bar") {
               HttpResponse(HttpStatusCode.OK, "hello")
            }
         }
         client.post("http://localhost:${server.port}/foo")
         client.delete("http://localhost:${server.port}/bar")
         server.invokedEndpoints() shouldBe listOf("/foo", "/bar")
      }
   }
}
