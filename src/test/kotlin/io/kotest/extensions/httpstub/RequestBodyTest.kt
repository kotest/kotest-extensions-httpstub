package io.kotest.extensions.httpstub

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode

class RequestBodyTest : FunSpec() {
   init {

      val client = HttpClient(Apache)
      val server = install(HttpStub)

      test("differentiate requests with request body") {
         server.mappings {
            post("/foo") {
               body("""{"test": true}""")
               HttpResponse(HttpStatusCode.OK, "true")
            }

            post("/foo") {
               body("""{"test": false}""")
               HttpResponse(HttpStatusCode.OK, "false")
            }
         }

         val resp = client.post(server.url("/foo")) {
            setBody("""{"test": true}""")
         }
         resp.status shouldBe HttpStatusCode.OK
         resp.bodyAsText() shouldBe "true"

         val resp2 = client.post(server.url("/foo")) {
            setBody("""{"test": false}""")
         }
         resp2.status shouldBe HttpStatusCode.OK
         resp2.bodyAsText() shouldBe "false"
      }
   }
}
