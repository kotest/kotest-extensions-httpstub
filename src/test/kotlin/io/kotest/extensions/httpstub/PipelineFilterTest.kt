package io.kotest.extensions.httpstub

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode

class PipelineFilterTest : FunSpec() {
   init {

      val client = HttpClient(Apache)
      val server = install(HttpStub)

      test("pipeline filters") {
         val invoked = mutableSetOf<String>()
         server.mappings {
            post("/foo") {
               HttpResponse(HttpStatusCode.OK, "hello")
                  .withHeader("myheader", "headerface")
            }
            listener { invoked.add(it.url) }
         }
         val resp = client.post("http://localhost:${server.port}/foo")
         resp.status shouldBe HttpStatusCode.OK
         resp.headers["myheader"] shouldBe "headerface"
      }
   }
}
