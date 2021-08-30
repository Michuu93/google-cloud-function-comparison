package com.mhoja.config

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

  def readConfig(args: Array[String] = Array(System.getProperty("project"), System.getProperty("token"))): TestConfig = {
    val defaultVarFile = readFile(defaultVarFilePath)
    val defaultVarParsed = new HCLParser().parse(defaultVarFile)
    val customVarFile = readFile(customVarFilePath)
    val customVarParsed = new HCLParser().parse(customVarFile)

    val regions = getRegions(defaultVarParsed, customVarParsed)
    val folders = getFolders(defaultVarParsed, customVarParsed)

    val project = args.head
    val token = args(1)

    val config = TestConfig(project, token, regions.asScala.toList, folders.asScala.toList)
    println(config)
    config
  }

  private def readFile(path: String): String = {
    val source = Source.fromFile(path)
    try source.getLines() mkString "\n" finally source.close()
  }

  private def getRegions(defaultVarParsed: util.Map[String, AnyRef], customVarParsed: util.Map[String, AnyRef]): util.ArrayList[String] = {
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

  private def getFolders(defaultVarParsed: util.Map[String, AnyRef], customVarParsed: util.Map[String, AnyRef]): util.List[String] = {
    Option(parseFolders(customVarParsed)).getOrElse(parseFolders(defaultVarParsed))
  }

  private def parseFolders(parsed: util.Map[String, AnyRef]): util.List[String] = {
    val variables = parseVariables(parsed).get("functions")
    if (variables == null) {
      null
    } else variables match {
      case functions: util.ArrayList[java.util.LinkedHashMap[String, String]] =>
        functions
          .stream()
          .map(function => function.get("folder")).collect(Collectors.toList[String])
      case functions: util.LinkedHashMap[String, util.ArrayList[util.LinkedHashMap[String, String]]] =>
        functions
          .get("default")
          .stream()
          .map(function => function.get("folder")).collect(Collectors.toList[String])
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

  private def getProjectPath: String = {
    val rootPath = os.pwd.toString()
    if (rootPath.endsWith("/tests")) {
      rootPath.substring(0, rootPath.length - "/tests".length)
    } else {
      rootPath
    }
  }

}

case class TestConfig(project: String, token: String, regions: List[String], folders: List[String]) {
  override def toString: String = "project=" + project + "\ntoken=" + token + "\nregions=" + regions + "\nfolders=" + folders
}
