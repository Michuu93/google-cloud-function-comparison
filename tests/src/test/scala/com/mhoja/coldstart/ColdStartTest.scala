package com.mhoja.coldstart

import com.mhoja.config.{ConfigReader, TestConfig}
import de.vandermeer.asciitable.{AsciiTable, CWC_LongestLine}
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment

import java.net.URI
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpClient, HttpRequest}
import scala.collection.mutable.ArrayBuffer

object ColdStartTest {
  val client: HttpClient = HttpClient.newHttpClient
  val requestsCount: Int = 5 // TODO as parameter

  def main(args: Array[String]): Unit = {
    val config: TestConfig = ConfigReader.readConfig(args)
    val table = new AsciiTable
    addTableHeader(table)

    config.regions.foreach(region => {
      config.folders.filter(!_.endsWith("heavy")).foreach(folder => {
        val functionName = region + "_" + folder
        val functionUrl = "https://" + region + "-" + config.project + ".cloudfunctions.net/" + functionName
        val responseTimes = makeRequests(functionUrl, config.token, requestsCount)
        val coldStartTime = responseTimes.head
        val averageTimeOfRemainingResponses = responseTimes.takeRight(requestsCount - 1).sum / (requestsCount - 1)
        val diff = coldStartTime - averageTimeOfRemainingResponses
        table.addRow(functionName, coldStartTime, averageTimeOfRemainingResponses, diff).setTextAlignment(TextAlignment.RIGHT)
        table.addRule()
      })
    })

    table.setPaddingLeftRight(1)
    println(table.render())
  }

  def makeRequests(functionUrl: String, token: String, requestsCount: Int): Array[Long] = {
    val responseTimes: ArrayBuffer[Long] = ArrayBuffer()
    for (_ <- 1 to requestsCount) {
      val request = HttpRequest.newBuilder.uri(URI.create(functionUrl)).header("Authorization", "bearer " + token).build
      val startTime = System.currentTimeMillis
      val response = client.send(request, BodyHandlers.ofString)
      // TODO check response status/body
      val elapsedTime = System.currentTimeMillis - startTime
      responseTimes += elapsedTime
    }
    responseTimes.toArray
  }

  def addTableHeader(table: AsciiTable): Unit = {
    table.getRenderer.setCWC(new CWC_LongestLine())
    table.addRule()
    table.addRow(null, null, null, "Results").setTextAlignment(TextAlignment.CENTER)
    table.addRule()
    table.addRow("Function name", "1st time [ms]", "avg remaining [ms]", "diff [ms]").setTextAlignment(TextAlignment.CENTER)
    table.addRule()
  }
}
