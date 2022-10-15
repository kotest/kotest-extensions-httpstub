package io.kotest.extensions.httpstub

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.http.HttpStatusCode

class MethodTest : FunSpec() {
   init {

      val client = HttpClient(Apache)

      test("post request") {
         val server = httpstub {
            post("/foo") {
               HttpResponse(HttpStatusCode.OK, "hello")
            }
         }
         val resp = client.post("http://localhost:${server.port}/foo")
         resp.status shouldBe HttpStatusCode.OK
      }

      test("get request") {
         val server = httpstub {
            get("/foo") {
               HttpResponse(HttpStatusCode.Created, "hello")
            }
         }
         val resp = client.get("http://localhost:${server.port}/foo")
         resp.status shouldBe HttpStatusCode.Created
      }

      test("patch request") {
         val server = httpstub {
            patch("/foo") {
               HttpResponse(HttpStatusCode.PartialContent, "hello")
            }
         }
         val resp = client.patch("http://localhost:${server.port}/foo")
         resp.status shouldBe HttpStatusCode.PartialContent
      }

      test("put request") {
         val server = httpstub {
            put("/foo") {
               HttpResponse(HttpStatusCode.ResetContent, "hello")
            }
         }
         val resp = client.put("http://localhost:${server.port}/foo")
         resp.status shouldBe HttpStatusCode.ResetContent
      }

      test("delete request") {
         val server = httpstub {
            delete("/foo") {
               HttpResponse(HttpStatusCode.NoContent, "hello")
            }
         }
         val resp = client.delete("http://localhost:${server.port}/foo")
         resp.status shouldBe HttpStatusCode.NoContent
      }
   }
}
