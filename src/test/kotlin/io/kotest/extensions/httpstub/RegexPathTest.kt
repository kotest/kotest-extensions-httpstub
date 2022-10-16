package io.kotest.extensions.httpstub

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode

class RegexPathTest : FunSpec() {
   init {

      val client = HttpClient(Apache)
      val server = install(HttpStub) {
         resetRequests = Reset.TEST
      }

      test("with regex path") {
         server.mappings {
            get("/foo/.*".toRegex()) {
               ok()
            }
         }
         val resp = client.get(server.url("/foo/a"))
         resp.status shouldBe HttpStatusCode.OK
         server.invokedEndpoints() shouldBe listOf("/foo/a")
      }

      test("regex special characters should work when not using regex") {
         server.mappings {
            get("/foo/*") {
               ok()
            }
            post("/bar?q=a") {
               ok()
            }
         }
         val resp1 = client.get(server.url("/foo/*"))
         val resp2 = client.post(server.url("/bar?q=a"))
         resp1.status shouldBe HttpStatusCode.OK
         resp2.status shouldBe HttpStatusCode.OK
         server.invokedEndpoints() shouldBe listOf("/foo/*", "/bar?q=a")
      }

      test("regex path with multiple parts") {
         server.mappings {
            post("/foo/.*/.*".toRegex()) {
               ok()
            }
         }
         val resp = client.post(server.url("/foo/a/b"))
         resp.status shouldBe HttpStatusCode.OK
         server.invokedEndpoints() shouldBe listOf("/foo/a/b")
      }

   }
}
