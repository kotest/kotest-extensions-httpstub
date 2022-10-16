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

   /**
    * Resolves an absolute url for the given [endpoint].
    *
    * @param endpoint the endpoint path, eg /users
    * @return the absolute url, eg http://localhost:4524/users
    */
   fun url(endpoint: String): String = server.url(endpoint)

   /**
    * @return true if the server is started and running
    */
   fun started() = server.isRunning

   /**
    * @return true if the server is not running.
    */
   fun stopped() = !server.isRunning

   /**
    * Returns a list of the endpoints invoked, eg "/foo", "/bar"
    */
   fun invokedEndpoints(): List<String> = requests.map { it.url }

   /**
    * Returns a list of the absolute urls invoked, eg "http://localhost:1234/foo", "http://localhost:1234/bar"
    */
   fun invokedUrls(): List<String> = requests.map { it.absoluteUrl }

   /**
    * A Kotest matcher that will fail a test if any requests were made which
    * were unmatched.
    */
   fun shouldNotHaveUnmatchedRequests() = server.checkForUnmatchedRequests()

   fun clearRequests() {
      requests.clear()
   }

   fun mappings(configure: HttpStubber.() -> Unit) {
      HttpStubber(server).configure()
   }

   fun mappings(vararg configures: HttpStubber.() -> Unit) {
      val stubber = HttpStubber(server)
      configures.forEach { it.invoke(stubber) }
   }
}
