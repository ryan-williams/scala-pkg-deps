package com.runsascoded.deps

import java.io.File

import org.apache.commons.io.FileUtils.listFiles

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scala.io.Source.fromFile

object Deps {

  val packageRE = """^package (.*)$""".r
  val importRE = """^import (.*)$""".r

  def main(args: Array[String]): Unit = {
    val rootDir =
      new File(
        args
          .headOption
          .getOrElse(".")
      )

    val scalaFiles = listFiles(rootDir, Array(".scala"), true).asScala

    for {
      scalaFile ← scalaFiles
    } {
      var pkg: String = _
      val imports = ArrayBuffer[Seq[String]]()
      fromFile(scalaFile).getLines().map {
        case packageRE(pkgStr) ⇒ pkg = pkgStr
        case importRE(importExpr) ⇒ imports += importExpr.split("\.")
      }
      println(s"File:\t$scalaFile")
      println(s"Package:\t$pkg")
      println("Imports:")
      println(imports.map(_.mkString(".")).mkString("\t", "\n\t", "\n"))
    }
  }
}
