package com.hoja

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.language.postfixOps

class FunctionsTest extends Simulation {

  val project: String = System.getProperty("project")
  val token: String = System.getProperty("token")
  val region: String = System.getProperty("region")
  val baseUrl: String = "https://" + region + "-" + project + ".cloudfunctions.net"
  println(s"FunctionsTest baseUrl=${baseUrl}")

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(baseUrl)
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
    .header("Authorization", "bearer " + token)

  val scn: ScenarioBuilder = scenario("FunctionsTest")
    .exec(http("request_go113")
      .get("/go113"))
    .exec(http("request_java11")
      .get("/java11"))
    .exec(http("request_nodejs14")
      .get("/nodejs14"))
    .exec(http("request_python39")
      .get("/python39"))
    .exec(http("request_ruby27")
      .get("/ruby27"))

  setUp(scn.inject(constantConcurrentUsers(10) during 60) //model zamknięty, kontrolujemy liczbe uzytkowników
    .protocols(httpProtocol))
    .assertions(global.successfulRequests.percent.is(100)) //asercja po testach
}
