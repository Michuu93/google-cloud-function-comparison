package com.mhoja.gatling

import com.mhoja.config.{ConfigReader, Function, TestConfig}
import io.gatling.core.Predef._
import io.gatling.core.structure.{PopulationBuilder, ScenarioBuilder}
import io.gatling.http.Predef._

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

case class ScenarioData(function: Function, region: String) {}

class FunctionsTest extends Simulation {
  val concurrentUsers: Int = System.getProperty("users", "20").toInt
  val testDuration: Int = System.getProperty("duration", "120").toInt
  val config: TestConfig = ConfigReader.readConfig()

  var scenarios: List[PopulationBuilder] = prepareScenarios()
  setUp(TestUtils.toSequential(scenarios)).assertions(global.successfulRequests.percent.is(100))

  def prepareScenarios(): List[PopulationBuilder] = {
    val populations: ListBuffer[PopulationBuilder] = ListBuffer()

    config.regions.flatMap(region => config.functions.map(function => ScenarioData(function, region))).foreach(data => {
      val baseUrl = "https://" + data.region + "-" + config.project + ".cloudfunctions.net"
      val functionName = data.function.getFunctionName(data.region)
      println(s"functionName=$functionName, baseUrl=$baseUrl")

      val scn: ScenarioBuilder = scenario(functionName)
        .group(data.region) {
          exec(http(functionName)
            .get("/" + functionName)
            .check(status.is(200), bodyString.is("Hello World!"))
          )
        }

      val population = scn.inject(constantConcurrentUsers(concurrentUsers) during testDuration)
        .protocols(prepareHttpProtocol(baseUrl))

      populations += population
    })

    populations.toList
  }

  private def prepareHttpProtocol(baseUrl: String) = {
    http
      .baseUrl(baseUrl)
      .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
      .acceptEncodingHeader("gzip, deflate")
      .acceptLanguageHeader("en-US,en;q=0.5")
      .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
      .header("Authorization", "bearer " + config.token)
  }
}
