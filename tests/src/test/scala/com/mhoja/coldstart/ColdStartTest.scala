package com.mhoja.coldstart

import com.mhoja.config.{ConfigReader, TestConfig}

object ColdStartTest {

  def main(args: Array[String]): Unit = {
    val config: TestConfig = ConfigReader.readConfig(args)
    config.regions.foreach(region => {
      config.folders.filter(!_.endsWith("heavy")).foreach(folder => {
        val baseUrl = "https://" + region + "-" + config.project + ".cloudfunctions.net"
        val functionName = region + "_" + folder
        println(s"functionName=${functionName}: baseUrl=$baseUrl")
      })
    })
  }

}
