package com.mhoja.coldstart

import com.mhoja.config.{ConfigReader, TestConfig}
import de.vandermeer.asciitable.{AsciiTable, CWC_LongestLine}
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment

import java.net.URI
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpClient, HttpRequest}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object ColdStartTest {
  val client: HttpClient = HttpClient.newHttpClient
  var requestsPerFunction: Int = 10


  def main(args: Array[String]): Unit = {
    if (args != null && args.length == 2) {
      requestsPerFunction = args(1).toInt
    }
    require(requestsPerFunction > 1, "Number of requests per function must be greater than 1")

    val config: TestConfig = ConfigReader.readConfig(args)
    val asciiTable = new AsciiTable
    addAsciiTableHeader(asciiTable)
    val htmlTable = new mutable.StringBuilder
    addHtmlTableHeader(htmlTable)

    config.regions.foreach(region => {
      config.functions.foreach(function => {
        val functionName = function.getFunctionName(region)
        val functionUrl = "https://" + region + "-" + config.project + ".cloudfunctions.net/" + functionName
        val responseTimes = makeRequests(functionUrl, config.getToken, requestsPerFunction)
        val coldStartTime = responseTimes.head
        val averageTimeOfRemainingResponses = responseTimes.takeRight(requestsPerFunction - 1).sum / (requestsPerFunction - 1)
        val diff = coldStartTime - averageTimeOfRemainingResponses
        asciiTable.addRow(functionName, coldStartTime, averageTimeOfRemainingResponses, diff).setTextAlignment(TextAlignment.RIGHT)
        asciiTable.addRule()
        addHtmlTableRow(htmlTable, function.runtime, region, coldStartTime, averageTimeOfRemainingResponses, diff)
      })
    })

    asciiTable.setPaddingLeftRight(1)
    println(asciiTable.render())

    addHtmlTableFooter(htmlTable)
    createHtmlTableFile(htmlTable)
  }

  def makeRequests(functionUrl: String, token: String, requestsCount: Int): Array[Long] = {
    val responseTimes: ArrayBuffer[Long] = ArrayBuffer()
    for (_ <- 1 to requestsCount) {
      val request = HttpRequest.newBuilder.uri(URI.create(functionUrl)).header("Authorization", "bearer " + token).build
      val startTime = System.currentTimeMillis
      val response = client.send(request, BodyHandlers.ofString)
      if (response.statusCode() != 200) {
        println(s"Request to ${functionUrl} returns status code ${response.statusCode()}")
      }
      val elapsedTime = System.currentTimeMillis - startTime
      responseTimes += elapsedTime
    }
    responseTimes.toArray
  }

  def addAsciiTableHeader(asciiTable: AsciiTable): Unit = {
    asciiTable.getRenderer.setCWC(new CWC_LongestLine())
    asciiTable.addRule()
    asciiTable.addRow(null, null, null, "Results").setTextAlignment(TextAlignment.CENTER)
    asciiTable.addRule()
    asciiTable.addRow("Function name", "1st time [ms]", "avg remaining [ms]", "diff [ms]").setTextAlignment(TextAlignment.CENTER)
    asciiTable.addRule()
  }

  def addHtmlTableHeader(htmlTable: mutable.StringBuilder): Unit = {
    htmlTable.append(
      """
<html>
  <head>
      <link href="https://cdn.jsdelivr.net/npm/simple-datatables@latest/dist/style.css" rel="stylesheet" type="text/css">
  </head>
  <body>
    <div id="coldstarts" class="tabcontent coldstarts">
      <table id="coldstarts_table">
        <thead>
          <tr>
            <th>Runtime</th>
            <th>Region</th>
            <th>1st time [ms]</th>
            <th>avg remaining [ms]</th>
            <th>diff [ms]</th>
          </tr>
        </thead>
        <tbody>""")
  }

  def addHtmlTableRow(htmlTable: mutable.StringBuilder, runtime: String, region: String, coldStartTime: Long, avgRemaining: Long, diff: Long): Unit = {
    htmlTable.append(
      s"""
      <tr>
        <td>${runtime}</td>
        <td>${region}</td>
        <td>${coldStartTime}</td>
        <td>${avgRemaining}</td>
        <td>${diff}</td>
      </tr>""")
  }

  def addHtmlTableFooter(htmlTable: mutable.StringBuilder): Unit = {
    htmlTable.append(
      """
        </tbody>
      </table>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/simple-datatables@latest" type="text/javascript"></script>
    <script type="text/javascript">
        const options = {
            searchable: false,
            paging: false,
            info: false
        };
        new simpleDatatables.DataTable("#coldstarts_table", options);
    </script>
  </body>
</html>""")
  }

  def createHtmlTableFile(htmlTable: mutable.StringBuilder): Unit = {
    val file = "target/coldstarts" + "_" + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now) + ".html"
    Files.write(Paths.get(file), htmlTable.toString().getBytes(StandardCharsets.UTF_8))
    println(s"Generated Simple-DataTables file, path=${file}");
  }
}
