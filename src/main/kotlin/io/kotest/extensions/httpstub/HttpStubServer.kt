package io.kotest.extensions.httpstub

import com.github.tomakehurst.wiremock.WireMockServer

data class HttpStubServer(val server: WireMockServer) {

   val port = server.port()
   val baseUrl: String = server.baseUrl()

   val isHttp = server.isHttpEnabled
   val isHttps = server.isHttpsEnabled

   private val requests = mutableListOf<HttpRequest>()

   init {
      server.addMockServiceRequestListener { request, _ ->
         requests.add(request.toHttpRequest())
      }
   }

   fun started() = server.isRunning
   fun stopped() = !server.isRunning

   /**
    * Returns a list of the endpoints invoked, eg "/foo", "/bar"
    */
   fun invokedEndpoints(): List<String> = requests.map { it.url }
   fun invokedUrls(): List<String> = requests.map { it.absoluteUrl }

   fun checkForUnmatchedRequests() = server.checkForUnmatchedRequests()

   fun clearRequests() {
      requests.clear()
   }

   fun mappings(configure: HttpStubber.() -> Unit) {
      HttpStubber(server).configure()
   }
}
