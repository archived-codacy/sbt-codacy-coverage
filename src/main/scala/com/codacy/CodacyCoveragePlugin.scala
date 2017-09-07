package com.codacy

import java.io.File

import com.codacy.api.client.{CodacyClient, FailedResponse, SuccessfulResponse}
import com.codacy.api.helpers.FileHelper
import com.codacy.api.service.CoverageServices
import com.codacy.api.{CoverageFileReport, Language}
import com.codacy.parsers.implementation.CoberturaParser
import rapture.json.{Json, _}
import sbt.Keys._
import sbt._

object CodacyCoveragePlugin extends AutoPlugin {

  implicit val (ast, stringParser, jsonSerializer) = {
    import rapture.json.jsonBackends.circe._
    (implicitJsonAst, implicitJsonStringParser, circeJValueSerializer)
  }

  private implicit lazy val ser = implicitly[Serializer[CoverageFileReport, Json]]

  object AutoImport {
    val codacyCoverage = taskKey[Unit]("Upload coverage reports to Codacy.")
    val codacyProjectToken = settingKey[Option[String]]("Your project token.")
    val coberturaFile = settingKey[File]("Path for project Cobertura file.")
    val codacyApiBaseUrl = settingKey[Option[String]]("The base URL for the Codacy API.")
    val codacyCommit = settingKey[Option[String]]("The commit uuid of the coverage.")

    lazy val baseSettings: Seq[Def.Setting[_]] = Seq(
      codacyCoverage := {
        codacyCoverageCommand(state.value, baseDirectory.value, coberturaFile.value,
          crossTarget.value / "coverage-report" / "codacy-coverage.json",
          codacyProjectToken.value, codacyApiBaseUrl.value, codacyCommit.value)
      },
      aggregate in codacyCoverage := false,
      codacyProjectToken := None,
      codacyApiBaseUrl := None,
      codacyCommit := None,
      coberturaFile := crossTarget.value / ("coverage-report" + File.separator + "cobertura.xml")
    )
  }

  import com.codacy.CodacyCoveragePlugin.AutoImport._

  override def trigger: PluginTrigger = allRequirements

  override val projectSettings = baseSettings

  private val publicApiBaseUrl = "https://api.codacy.com"

  private def codacyCoverageCommand(state: State, rootProjectDir: File, coberturaFile: File, codacyCoverageFile: File,
                                    codacyToken: Option[String], codacyApiBaseUrl: Option[String],
                                    sbtCodacyCommit: Option[String]): Unit = {
    implicit val logger: Logger = state.log

    val commitUUIDOpt = sbtCodacyCommit orElse
      getNonEmptyEnv("CI_COMMIT") orElse
      getNonEmptyEnv("TRAVIS_PULL_REQUEST_SHA") orElse
      getNonEmptyEnv("TRAVIS_COMMIT") orElse
      getNonEmptyEnv("DRONE_COMMIT") orElse
      getNonEmptyEnv("CIRCLE_SHA1") orElse
      getNonEmptyEnv("CI_COMMIT_ID") orElse
      getNonEmptyEnv("WERCKER_GIT_COMMIT")

    FileHelper.withTokenAndCommit(codacyToken, commitUUIDOpt) {
      case (projectToken, commitUUID) =>

        val reader = new CoberturaParser(Language.Scala, rootProjectDir, coberturaFile)
        val report = reader.generateReport()

        FileHelper.writeJsonToFile(codacyCoverageFile, report)

        val codacyClient = new CodacyClient(apiBaseUrl(codacyApiBaseUrl), projectToken = Some(projectToken))
        val coverageServices = new CoverageServices(codacyClient)

        logger.info(s"Uploading coverage data...")

        coverageServices.sendReport(commitUUID, Language.Scala.toString, report) match {
          case FailedResponse(error) =>
            sys.error(s"Failed to upload data. Reason: $error")
            state.exit(ok = false)
            Left(error)
          case SuccessfulResponse(response) =>
            logger.success(s"Coverage data uploaded. ${response.success}")
            Right(state)
        }
    } match {
      case Left(error) =>
        sys.error(error)
        state.exit(ok = false)
      case _ =>
    }
  }

  private def getNonEmptyEnv(key: String): Option[String] = {
    sys.env.get(key).filter(_.trim.nonEmpty)
  }

  private def apiBaseUrl(codacyApiBaseUrl: Option[String]): Option[String] = {
    // Check for an environment variable to override the API URL.
    // If it doesn't exist, try the build options or default to the public API.
    codacyApiBaseUrl orElse
      getNonEmptyEnv("CODACY_API_BASE_URL") orElse
      Some(publicApiBaseUrl)
  }

}
