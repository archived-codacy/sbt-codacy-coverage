package com.codacy.parsers

import java.io.File

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.collection.mutable.ArrayBuffer

class CoberturaReaderTest extends WordSpec with BeforeAndAfterAll with Matchers {

  val reader = new CoberturaParser(new File("src/test/resources/test_cobertura.xml"), new File(""), new File(""))

  "CoberturaReader" when {
    "reading a Cobertura file" should {

      "return a valid report" in {
        val fileReport = reader.generateReport()
        fileReport.total should equal(87)
        fileReport.fileReports.map(_.filename).head should endWith("src/test/resources/TestSourceFile.scala")
        fileReport.fileReports.map(_.total).head should equal(87)
        fileReport.fileReports.map(_.coverage) should equal(
          ArrayBuffer(Map("4" -> 1, "9" -> 1, "5" -> 1, "10" -> 1, "6" -> 2), Map("1" -> 1, "2" -> 1, "3" -> 1))
        )
      }

    }
  }
}
