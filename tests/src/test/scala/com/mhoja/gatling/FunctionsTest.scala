package com.mhoja.gatling

import com.mhoja.config.{ConfigReader, TestConfig}
import io.gatling.core.Predef._
import io.gatling.core.structure.{PopulationBuilder, ScenarioBuilder}
import io.gatling.http.Predef._

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

case class ScenarioData(region: String, folder: String) {}

class FunctionsTest extends Simulation {
  val config: TestConfig = ConfigReader.readConfig()

  var scenarios: List[PopulationBuilder] = prepareScenarios()
  setUp(TestUtils.toSequential(scenarios)).assertions(global.successfulRequests.percent.is(100))

  def prepareScenarios(): List[PopulationBuilder] = {
    val populations: ListBuffer[PopulationBuilder] = ListBuffer()

    config.regions.flatMap(region => config.folders.filter(!_.endsWith("heavy")).map(folder => ScenarioData(region, folder))).foreach(data => {
      val baseUrl = "https://" + data.region + "-" + config.project + ".cloudfunctions.net"
      val functionName = data.region + "_" + data.folder
      println(s"functionName=$functionName, baseUrl=$baseUrl")

      val scn: ScenarioBuilder = scenario("FunctionsTest_" + data.region + "_" + data.folder)
        .group(data.region) {
          exec(http(functionName)
            .get("/" + functionName)
            .check(status.is(200), bodyString.is("Hello World!"))
          )
        }

      val population = scn.inject(constantConcurrentUsers(20) during 120)
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
