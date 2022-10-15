package io.kotest.extensions.httpstub

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode

class ResponseHeaderTest : FunSpec() {
   init {

      val client = HttpClient(Apache)
      val server = install(HttpStub) {
         resetMappings = Reset.TEST
      }

      test("with response header") {
         server.mappings {
            post("/foo") {
               HttpResponse(HttpStatusCode.OK, "hello")
                  .withHeader("myheader", "headerface")
            }
         }
         val resp = client.post("http://localhost:${server.port}/foo")
         resp.status shouldBe HttpStatusCode.OK
         resp.headers["myheader"] shouldBe "headerface"
      }

      test("with multiple headers") {
         server.mappings {
            post("/foo") {
               HttpResponse(HttpStatusCode.OK, "hello")
                  .withHeader("myheader1", "headerface")
                  .withHeader("myheader2", "headerfoot")
            }
         }
         val resp = client.post("http://localhost:${server.port}/foo")
         resp.status shouldBe HttpStatusCode.OK
         resp.headers["myheader1"] shouldBe "headerface"
         resp.headers["myheader2"] shouldBe "headerfoot"
      }

      test("with duplicated header") {
         server.mappings {
            post("/foo") {
               HttpResponse(HttpStatusCode.OK, "hello")
                  .withHeader("myheader", "headerface")
                  .withHeader("myheader", "headerface")
            }
         }
         val resp = client.post("http://localhost:${server.port}/foo")
         resp.status shouldBe HttpStatusCode.OK
         resp.headers["myheader"] shouldBe "headerface"
      }

      test("with explicit content type support") {
         server.mappings {
            post("/foo") {
               HttpResponse(HttpStatusCode.OK, "hello")
                  .withContentType(ContentType.Application.Json)
            }
         }
         val resp = client.post("${server.baseUrl}/foo")
         resp.status shouldBe HttpStatusCode.OK
         resp.headers[HttpHeaders.ContentType] shouldBe "application/json"
      }

      test("explicit content type support should overwrite previous header") {
         server.mappings {
            post("/foo") {
               HttpResponse(HttpStatusCode.OK, "hello")
                  .withHeader(HttpHeaders.ContentType, "foo")
                  .withContentType(ContentType.Application.Json)
            }
         }
         val resp = client.post("http://localhost:${server.port}/foo")
         resp.status shouldBe HttpStatusCode.OK
         resp.headers[HttpHeaders.ContentType] shouldBe "application/json"
      }
   }
}
