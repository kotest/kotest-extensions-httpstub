package io.kotest.extensions.httpstub

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode

class ResponseBodyTest : FunSpec() {
   init {

      val client = HttpClient(Apache)

      test("with response body") {
         val server = httpstub {
            post("/foo") {
               HttpResponse(HttpStatusCode.OK, "hello")
            }
         }
         val resp = client.post("http://localhost:${server.port}/foo")
         resp.status shouldBe HttpStatusCode.OK
         resp.bodyAsText() shouldBe "hello"
      }

      test("with response body json") {
         val server = httpstub {
            get("/foo") {
               HttpResponse(HttpStatusCode.OK, """{"foo":"bar"}""")
            }
         }
         val resp = client.get("http://localhost:${server.port}/foo")
         resp.status shouldBe HttpStatusCode.OK
         resp.bodyAsText() shouldBe """{"foo":"bar"}"""
      }
   }
}
