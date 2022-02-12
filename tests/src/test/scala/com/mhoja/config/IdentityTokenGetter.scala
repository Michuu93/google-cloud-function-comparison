package com.mhoja.config

import scala.sys.process._

object IdentityTokenGetter {
  private val identityTokenCommand = "gcloud auth print-identity-token"

  def main(args: Array[String]): Unit = {
    println(getIdentityToken)
  }

  private[config] def getIdentityToken: String = {
    println(s"Getting identity token by command '${identityTokenCommand}'")
    Process(identityTokenCommand).!!.trim
  }

}
