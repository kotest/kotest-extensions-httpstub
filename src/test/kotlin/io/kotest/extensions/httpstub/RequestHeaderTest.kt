package io.kotest.extensions.httpstub

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode

class RequestHeaderTest : FunSpec() {
   init {

      val client = HttpClient(Apache)
      val server = install(HttpStub)

      test("with request header") {
         server.mappings {
            post("/foo") {
               header("Accept", "foo")
               ok()
            }
         }
         val resp1 = client.post(server.url("/foo"))
         resp1.status shouldBe HttpStatusCode.NotFound

         val resp2 = client.post(server.url("/foo")) {
            header("Accept", "foo")
         }
         resp2.status shouldBe HttpStatusCode.OK

         val resp3 = client.post(server.url("/foo")) {
            header("Accept", "asdsd")
         }
         resp3.status shouldBe HttpStatusCode.NotFound
      }

      test("with multiple request headers") {
         server.mappings {
            post("/foo") {
               header("header1", "foo")
               header("header2", "bar")
               ok()
            }
         }
         val resp1 = client.post(server.url("/foo"))
         resp1.status shouldBe HttpStatusCode.NotFound

         val resp2 = client.post(server.url("/foo")) {
            header("header1", "foo")
         }
         resp2.status shouldBe HttpStatusCode.NotFound

         val resp3 = client.post(server.url("/foo")) {
            header("header1", "foo")
            header("header2", "bar")
         }
         resp3.status shouldBe HttpStatusCode.OK
      }
   }
}
