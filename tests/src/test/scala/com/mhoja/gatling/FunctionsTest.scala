package com.mhoja.gatling

import com.mhoja.config.{ConfigReader, Function, TestConfig}
import io.gatling.core.Predef._
import io.gatling.core.structure.{PopulationBuilder, ScenarioBuilder}
import io.gatling.http.Predef._

import scala.util.Random
import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

case class ScenarioData(function: Function, region: String) {}

class FunctionsTest extends Simulation {
  val concurrentUsers: Int = System.getProperty("users", "20").toInt
  val testDuration: Int = System.getProperty("duration", "120").toInt
  val variant: String = System.getProperty("variant", "light")
  println(s"concurrentUsers = ${concurrentUsers}, testDuration = ${testDuration}, variant = ${variant}")
  val config: TestConfig = ConfigReader.readConfig()

  var scenarios: List[PopulationBuilder] = prepareScenarios()
  setUp(TestUtils.toSequential(scenarios)).assertions(global.successfulRequests.percent.is(100))

  def prepareScenarios(): List[PopulationBuilder] = {
    val populations: ListBuffer[PopulationBuilder] = ListBuffer()

    config.regions.flatMap(region => config.functions.map(function => ScenarioData(function, region))).foreach(data => {
      val baseUrl = "https://" + data.region + "-" + config.project + ".cloudfunctions.net"
      val functionName = data.function.getFunctionName(data.region)
      println(s"functionName=$functionName, baseUrl=$baseUrl")

      val randomString: Iterator[Map[String, String]] = Iterator.continually(Map("randstring" -> Random.alphanumeric.take(1024).mkString))

      val scn: ScenarioBuilder = if (variant.equals("heavy")) scenario(functionName)
        .feed(randomString)
        .exec(session => {
          session.set("identityToken", config.getToken)
        })
        .group(data.region) {
          exec(
            http(functionName)
              .post("/" + functionName)
              .header("content-type", "content-type:text/plain")
              .body(StringBody("${randstring}"))
              .header("Authorization", "bearer ${identityToken}")
              .check(
                status.saveAs("httpStatus"),
                status.is(200),
                checkIf(session => session("httpStatus").as[Integer].equals(200)) {
                  bodyString.transform(_.length > 1).is(true)
                }
              )
          ).doIf(session => session("httpStatus").as[Integer].equals(401)) {
            exec(session => {
              val newToken = config.getNewToken
              println(s"Refreshed token=${newToken}")
              session.remove("httpStatus").set("identityToken", newToken)
            })
          }
        } else scenario(functionName)
        .exec(session => {
          session.set("identityToken", config.getToken)
        })
        .group(data.region) {
          exec(
            http(functionName)
              .get("/" + functionName)
              .header("Authorization", "bearer ${identityToken}")
              .check(
                status.saveAs("httpStatus"),
                status.is(200),
                checkIf(session => session("httpStatus").as[Integer].equals(200)) {
                  bodyString.is("Hello World!")
                }
              )
          ).doIf(session => session("httpStatus").as[Integer].equals(401)) {
            exec(session => {
              val newToken = config.getNewToken
              println(s"Refreshed token=${newToken}")
              session.remove("httpStatus").set("identityToken", newToken)
            })
          }
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
  }
}
