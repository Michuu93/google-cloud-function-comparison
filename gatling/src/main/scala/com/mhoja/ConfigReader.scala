package com.mhoja

import com.bertramlabs.plugins.hcl4j.HCLParser

import java.util
import java.util.stream.Collectors
import scala.io.Source
import scala.jdk.CollectionConverters.CollectionHasAsScala


object ConfigReader {
  private val defaultVarFileName: String = "variables.tf"
  private val customVarFileName: String = "terraform.tfvars"
  private val terraformPath: String = "/terraform/"
  private val defaultVarFilePath: String = getProjectPath + terraformPath + defaultVarFileName
  private val customVarFilePath: String = getProjectPath + terraformPath + customVarFileName

  def main(args: Array[String]): Unit = {
    readConfig()
  }

  def readConfig(): TestConfig = {
    val defaultVarFile = readFile(defaultVarFilePath)
    val defaultVarParsed = new HCLParser().parse(defaultVarFile)
    val customVarFile = readFile(customVarFilePath)
    val customVarParsed = new HCLParser().parse(customVarFile)

    val regions = getRegions(defaultVarParsed, customVarParsed)
    val runtimes = getRuntimes(defaultVarParsed, customVarParsed)

    val project: String = System.getProperty("project")
    val token: String = System.getProperty("token")

    val config = TestConfig(project, token, regions.asScala.toList, runtimes.asScala.toList)
    println(config)
    config
  }

  private def readFile(path: String): String = {
    val source = Source.fromFile(path)
    try source.getLines mkString "\n" finally source.close()
  }

  private def getRegions(defaultVarParsed: util.Map[String, AnyRef], customVarParsed: util.Map[String, AnyRef]) = {
    Option(parseRegions(customVarParsed)).getOrElse(parseRegions(defaultVarParsed))
  }

  private def parseRegions(parsed: util.Map[String, AnyRef]): util.ArrayList[String] = {
    val variables = parseVariables(parsed).get("function_regions")
    if (variables == null) {
      null
    } else variables match {
      case regions: util.ArrayList[String] =>
        regions
      case regions: util.LinkedHashMap[String, util.ArrayList[String]] =>
        regions.get("default")
    }
  }

  private def parseVariables(parsed: util.Map[String, AnyRef]): util.LinkedHashMap[String, AnyRef] = {
    val variables = parsed.get("variable")
    if (variables == null) {
      parsed.asInstanceOf[util.LinkedHashMap[String, AnyRef]]
    } else {
      variables.asInstanceOf[util.LinkedHashMap[String, AnyRef]]
    }
  }

  private def getRuntimes(defaultVarParsed: util.Map[String, AnyRef], customVarParsed: util.Map[String, AnyRef]): util.List[String] = {
    Option(parseRuntimes(customVarParsed)).getOrElse(parseRuntimes(defaultVarParsed))
  }

  private def parseRuntimes(parsed: util.Map[String, AnyRef]): util.List[String] = {
    val variables = parseVariables(parsed).get("functions")
    if (variables == null) {
      null
    } else variables match {
      case functions: util.ArrayList[java.util.LinkedHashMap[String, String]] =>
        functions
          .stream()
          .map(function => function.get("runtime")).collect(Collectors.toList[String])
      case functions: util.LinkedHashMap[String, util.ArrayList[util.LinkedHashMap[String, String]]] =>
        functions
          .get("default")
          .stream()
          .map(function => function.get("runtime")).collect(Collectors.toList[String])
    }
  }

  private def getProjectPath: String = {
    val rootPath = os.pwd.toString()
    if (rootPath.endsWith("/gatling")) {
      rootPath.substring(0, rootPath.length - "/gatling".length)
    } else {
      rootPath
    }
  }

}

case class TestConfig(val project: String, val token: String, val regions: List[String], val runtimes: List[String]) {
  override def toString: String = "project=" + project + "\ntoken=" + token + "\nregions=" + regions + "\nruntimes=" + runtimes
}
