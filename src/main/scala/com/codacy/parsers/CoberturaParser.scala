package com.codacy.parsers

import java.io.File

import com.codacy.api.{CodacyCoverageFileReport, CodacyCoverageReport}

import scala.xml.factory.XMLLoader
import scala.xml.{SAXParser, Elem, Node}

object XML extends XMLLoader[Elem] {
  override def parser: SAXParser = {
    val f = javax.xml.parsers.SAXParserFactory.newInstance()
    f.setNamespaceAware(false)
    f.setValidating(false)
    f.setFeature("http://xml.org/sax/features/namespaces", false)
    f.setFeature("http://xml.org/sax/features/validation", false)
    f.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false)
    f.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    f.newSAXParser()
  }
}

class CoberturaParser(coberturaFile: File, rootProject: File) {

  val elem = XML.loadFile(coberturaFile)

  val rootProjectDir = rootProject.getAbsolutePath + File.separator

  def generateReport(): CodacyCoverageReport = {
    val total = (elem \\ "coverage" \ "@line-rate").headOption.map {
      total =>
        (total.text.toFloat * 100).toInt
    }.getOrElse(0)

    val files = (elem \\ "class" \\ "@filename").map(_.text).toSet

    val filesCoverage = files.map {
      file =>
        lineCoverage(file)
    }.toSeq

    CodacyCoverageReport(total, filesCoverage)
  }

  private def lineCoverage(sourceFilename: String): CodacyCoverageFileReport = {
    val file = (elem \\ "class").filter {
      n: Node =>
        (n \\ "@filename").text == sourceFilename
    }

    val classHit = (file \\ "@line-rate").map {
      total =>
        (total.text.toFloat * 100).toInt
    }

    val fileHit = classHit.sum / classHit.length

    val lineHitMap = file.map {
      n =>
        (n \\ "line").map {
          line =>
            (line \ "@number").text.toInt -> (line \ "@hits").text.toInt
        }
    }.flatten.toMap.collect {
      case (key, value) if value > 0 =>
        key -> value
    }

    CodacyCoverageFileReport(sourceFilename.stripPrefix(rootProjectDir), fileHit, lineHitMap)
  }

}
