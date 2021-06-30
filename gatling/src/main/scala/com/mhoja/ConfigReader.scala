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

  def main(args: Array[String]): Unit = {
    println(readConfig().toString)
  }

  def readConfig(): FunctionConfig = {
    // TODO parse and merge custom variables
    val defaultVarFilePath = getProjectPath + terraformPath + defaultVarFileName;
    val defaultVarFile = readFile(defaultVarFilePath)
    val defaultVarParsed = new HCLParser().parse(defaultVarFile)

    val regions = getRegions(defaultVarParsed)
    val functions = getFunctions(defaultVarParsed)
    new FunctionConfig(regions.asScala.toList, functions.asScala.toList)
  }

  private def getProjectPath: String = {
    val rootPath = os.pwd.toString()
    if (rootPath.endsWith("/gatling")) {
      rootPath.substring(0, rootPath.length - "/gatling".length)
    } else {
      rootPath
    }
  }

  private def readFile(path: String): String = {
    val source = Source.fromFile(path)
    try source.getLines mkString "\n" finally source.close()
  }

  private def getRegions(parsed: util.Map[String, Object]): util.ArrayList[String] = {
    val variables = getVariables(parsed)
    variables.get("function_regions").asInstanceOf[util.LinkedHashMap[String, util.ArrayList[String]]].get("default")
  }

  private def getVariables(parsed: util.Map[String, Object]): util.LinkedHashMap[String, Object] = {
    parsed.get("variable").asInstanceOf[util.LinkedHashMap[String, Object]]
  }

  private def getFunctions(parsed: util.Map[String, Object]): util.List[String] = {
    val variables = getVariables(parsed)
    variables.get("functions").asInstanceOf[util.LinkedHashMap[String, util.ArrayList[String]]]
      .get("default").asInstanceOf[util.ArrayList[java.util.LinkedHashMap[String, String]]]
      .stream()
      .map(function => function.get("runtime")).collect(Collectors.toList[String])
  }

}

class FunctionConfig(val regions: List[String], val runtimes: List[String]) {
  override def toString: String = "regions=" + regions + "\nruntimes=" + runtimes
}
