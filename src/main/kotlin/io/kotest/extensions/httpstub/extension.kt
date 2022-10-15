package io.kotest.extensions.httpstub

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import kotlin.random.Random

class HttpStubConfig {
   var port: Int? = null
   var host: String = DEFAULT_HOST
   var resetRequests: Reset = Reset.SPEC
   var resetMappings: Reset = Reset.SPEC
}

enum class Reset {
   TEST, SPEC
}

object HttpStub : MountableExtension<HttpStubConfig, HttpStubServer>,
   BeforeTestListener,
   BeforeSpecListener,
   AfterSpecListener {

   private var server: WireMockServer? = null
   private var stub: HttpStubServer? = null
   private val config = HttpStubConfig()

   override fun mount(configure: HttpStubConfig.() -> Unit): HttpStubServer {
      config.configure()
      val p = config.port ?: Random.nextInt(10000, 65000)
      val s = WireMockServer(WireMockConfiguration.wireMockConfig().port(p).bindAddress(config.host))
      s.start()
      server = s
      return HttpStubServer(s).also { this.stub = it }
   }

   override suspend fun beforeTest(testCase: TestCase) {
      if (config.resetMappings == Reset.TEST) server?.resetMappings()
      if (config.resetRequests == Reset.TEST) {
         server?.resetRequests()
         stub?.clearRequests()
      }
   }

   override suspend fun afterSpec(spec: Spec) {
      if (config.resetMappings == Reset.SPEC) server?.resetMappings()
      if (config.resetRequests == Reset.SPEC) {
         server?.resetRequests()
         stub?.clearRequests()
      }
      server?.stop()
   }
}
