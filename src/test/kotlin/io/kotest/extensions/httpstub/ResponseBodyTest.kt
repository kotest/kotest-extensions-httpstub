package io.kotest.extensions.httpstub

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class ResponseBodyTest : FunSpec() {
   init {

      val client = HttpClient(Apache)
      val server = install(HttpStub) {
         resetMappings = Reset.TEST
      }

      test("with response body") {
         server.mappings {
            post("/foo") {
               HttpResponse(HttpStatusCode.OK, "hello")
            }
         }
         val resp = client.post("http://localhost:${server.port}/foo")
         resp.status shouldBe HttpStatusCode.OK
         resp.bodyAsText() shouldBe "hello"
      }

      test("with response body json") {
         server.mappings {
            get("/foo") {
               HttpResponse(HttpStatusCode.OK, """{"foo":"bar"}""")
            }
         }
         val resp = client.get("http://localhost:${server.port}/foo")
         resp.status shouldBe HttpStatusCode.OK
         resp.bodyAsText() shouldBe """{"foo":"bar"}"""
      }

      test("with json helper") {
         server.mappings {
            get("/foo") {
               okJson("""{"foo":"bar"}""")
            }
         }
         val resp = client.get("http://localhost:${server.port}/foo")
         resp.status shouldBe HttpStatusCode.OK
         resp.bodyAsText() shouldBe """{"foo":"bar"}"""
         resp.contentType() shouldBe ContentType.Application.Json
      }
   }
}
