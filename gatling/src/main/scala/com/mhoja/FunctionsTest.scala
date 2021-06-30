package com.mhoja

import io.gatling.core.Predef._
import io.gatling.core.structure.{PopulationBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

class FunctionsTest extends Simulation {
  val project: String = System.getProperty("project")
  val token: String = System.getProperty("token")
  val config: FunctionConfig = ConfigReader.readConfig()
  println(config)

  setUp(configurePopulations()).assertions(global.successfulRequests.percent.is(100)) //asercja po testach

  def configurePopulations(): List[PopulationBuilder] = {
    val populations: ListBuffer[PopulationBuilder] = ListBuffer()

    config.regions.foreach(region => {
      val baseUrl: String = "https://" + region + "-" + project + ".cloudfunctions.net"
      println(s"baseUrl=$baseUrl")

      var httpProtocol: HttpProtocolBuilder = http
        .baseUrl(baseUrl)
        .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        .acceptEncodingHeader("gzip, deflate")
        .acceptLanguageHeader("en-US,en;q=0.5")
        .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
        .header("Authorization", "bearer " + token)

      var scn: ScenarioBuilder = scenario("FunctionsTest_" + region)
      config.runtimes.foreach(runtime => {
        val functionName = region + "_" + runtime
        scn = scn.exec(http(functionName)
          .get("/" + functionName)
          .check(status.is(200), bodyString.is("Hello World!"))
        )
      })

      val population = scn.inject(constantConcurrentUsers(10) during 60) //model zamknięty, kontrolujemy liczbe uzytkowników
        .protocols(httpProtocol)

      populations += population
    })

    populations.toList
  }
}
