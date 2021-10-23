package com.mhoja.availability

import com.mhoja.config.{ConfigReader, TestConfig}

import java.net.URI
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpClient, HttpRequest}

object AvailabilityTest {
  val client: HttpClient = HttpClient.newHttpClient

  def main(args: Array[String]): Unit = {
    val config: TestConfig = ConfigReader.readConfig(args)

    config.regions.foreach(region => {
      config.functions.foreach(function => {
        val functionName = function.getFunctionName(region)
        val functionUrl = "https://" + region + "-" + config.project + ".cloudfunctions.net/" + functionName
        makeRequest(functionUrl, config.token)
      })
    })
  }

  def makeRequest(functionUrl: String, token: String): Unit = {
    val request = HttpRequest.newBuilder.uri(URI.create(functionUrl)).header("Authorization", "bearer " + token).build
    val response = client.send(request, BodyHandlers.ofString)
    if (response.statusCode() == 200) {
      println(s"OK ${functionUrl}")
    } else {
      println(s"ERROR ${functionUrl} returns status code ${response.statusCode()}")
    }
  }
}
