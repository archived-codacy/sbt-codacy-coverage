package com.codacy.api

import play.api.libs.json._

case class CodacyCoverageFileReport(filename: String, total: Int, coverage: Map[Int, Int])

case class CodacyCoverageReport(total: Int, fileReports: Seq[CodacyCoverageFileReport])

object CodacyCoverageReport {

  val intMapReads: Reads[Map[Int, Int]] = Reads { (json: JsValue) =>
    json match {
      case JsObject(fields) => JsSuccess(
        fields.collect {
          case (key, JsNumber(value)) =>
            key.toInt -> value.toInt
        }.toMap
      )
      case _ => JsError()
    }
  }

  val intMapWrites: Writes[Map[Int, Int]] = Writes { (intMap: Map[Int, Int]) =>
    val stringMap = intMap.map {
      case (key, value) =>
        key.toString -> value
    }

    Json.toJson(stringMap)
  }

  implicit val intMapFormat: Format[Map[Int, Int]] =
    Format(intMapReads, intMapWrites)

  implicit lazy val codacyCoverageFileReportFormat = Json.format[CodacyCoverageFileReport]
  implicit lazy val codacyCoverageReportFormat = Json.format[CodacyCoverageReport]

}
