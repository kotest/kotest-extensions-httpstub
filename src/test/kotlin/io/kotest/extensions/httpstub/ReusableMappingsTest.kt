package io.kotest.extensions.httpstub

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.delete
import io.ktor.http.HttpStatusCode

class ReusableMappingsTest : FunSpec() {
   init {

      val client = HttpClient(Apache)
      val server = install(HttpStub)

      val mappings = mappings {
         delete("/foo") {
            ok()
         }
      }

      test("with reusable mappings") {
         server.mappings(mappings)
         val resp = client.delete(server.url("/foo"))
         resp.status shouldBe HttpStatusCode.OK
      }
   }
}
