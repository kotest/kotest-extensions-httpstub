package io.kotest.extensions.httpstub

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

class RegexPathTest : FunSpec() {
   init {

      val client = HttpClient(Apache)
      val server = install(HttpStub) {
         resetRequests = Reset.TEST
      }

      test("with regex path") {
         server.mappings {
            get("/foo/.*") {
               ok()
            }
         }
         val resp = client.get(server.url("/foo/a"))
         resp.status shouldBe HttpStatusCode.OK
         server.invokedEndpoints() shouldBe listOf("/foo/a")
      }

      test("regex path with multiple parts") {
         server.mappings {
            get("/foo/.*/.*") {
               ok()
            }
         }
         val resp = client.get(server.url("/foo/a/b"))
         resp.status shouldBe HttpStatusCode.OK
         server.invokedEndpoints() shouldBe listOf("/foo/a/b")
      }

   }
}
