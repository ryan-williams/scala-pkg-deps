package com.runsascoded.deps

import java.io.{ BufferedWriter, File, FileWriter }

import org.apache.commons.io.FileUtils.listFiles

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source.fromFile
import scalax.collection.Graph
import scalax.collection.GraphEdge._
import scalax.collection.GraphPredef._
import scalax.collection.io.dot._
import scalax.collection.io.dot.implicits._

object InterPackageDepsToDot {

  val packageRE = """^package ([a-zA-Z_0-9\.]+)$""".r
  val packageObjectRE = """^package object ([^\s\{]+).*""".r
  val importRE = """import (.+)""".r

  def main(args: Array[String]): Unit = {
    val rootDir =
      new File(
        args
          .headOption
          .getOrElse(".")
      )

    val outFileOpt =
      if (args.length >= 2)
        Some(args(1))
      else
        None

    val scalaFiles = listFiles(rootDir, Array("scala"), true).asScala

    val pkgImportMap = mutable.Map[Symbol, ArrayBuffer[Symbol]]()

    for {
      scalaFile ← scalaFiles
    } {
      var pkgOpt: Option[Symbol] = None
      val imports = ArrayBuffer[Symbol]()
      fromFile(scalaFile)
        .getLines()
        .map(_.trim())
        .foreach {
          case packageRE(pkgStr) ⇒
            pkgOpt =
              Some(
                pkgOpt.getOrElse(Symbol(Seq())) ++ pkgStr.split("\\.")
              )

          case packageObjectRE(pkgStr) ⇒
            pkgOpt =
              Some(
                pkgOpt.getOrElse(Symbol(Seq())) ++ pkgStr.split("\\.")
              )

          case importRE(importExpr) ⇒
            imports += importExpr.split("\\.")

          case _ ⇒
        }

      pkgOpt match {
        case Some(pkg) ⇒
          pkgImportMap.getOrElseUpdate(pkg, ArrayBuffer[Symbol]()) ++= imports
        case None ⇒
          throw new Exception(s"Couldn't find a package declaration in $scalaFile")
      }
    }

    def mapImportToPackage(impt: Symbol): Option[Symbol] =
      impt.size match {
        case 0 ⇒ None
        case size ⇒
          pkgImportMap.get(impt) match {
            case Some(_) ⇒
              Some(impt)
            case None ⇒
              mapImportToPackage(impt.slice(0, size - 1))
          }
      }

    val pkgDepMap =
      for {
        (pkg, imports) ← pkgImportMap
      } yield
        pkg →
          (
            for {
              impt ← imports
              importPkg ← mapImportToPackage(impt)
              if pkg != importPkg  // Don't bother tracking self-edges
            } yield
              importPkg
          ).toSet

    val g =
      Graph(
        (for {
          (pkg, deps) ← pkgDepMap.toSeq
          dep ← deps
        } yield
          pkg ~> dep
        ): _*
      )

    val root = DotRootGraph(directed = true, id = Some("deps"), attrStmts = Nil, attrList = Nil)

    def edgeTransformer(innerEdge: Graph[Symbol, DiEdge]#EdgeT): Option[(DotGraph,DotEdgeStmt)] =
      innerEdge.edge match {
      case DiEdge(source, target) =>
        Some(
          (root,
            DotEdgeStmt(
              source.toString,
              target.toString,
              Nil
            )
          )
        )
      }

    val dotOutput = g.toDot(root, edgeTransformer)

    outFileOpt match {
      case Some(outFile) ⇒
        val writer = new BufferedWriter(new FileWriter(outFile))
        writer.write(dotOutput)
        writer.close()
      case None ⇒
        println(dotOutput)
    }
  }
}

case class Symbol(pieces: Seq[String]) extends AnyVal {
  override def toString: String = pieces.mkString(".")
}

object Symbol {
  implicit def make(pieces: Seq[String]): Symbol = new Symbol(pieces)
  implicit def make(pieces: Array[String]): Symbol = new Symbol(pieces)
  implicit def unpack(symbol: Symbol): Seq[String] = symbol.pieces
}
