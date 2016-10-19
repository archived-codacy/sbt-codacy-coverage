package com.codacy

import java.io.File

import com.codacy.api.Language
import com.codacy.api.client.CodacyClient
import com.codacy.api.helpers.FileHelper
import com.codacy.api.service.CoverageServices
import com.codacy.parsers.implementation.CoberturaParser
import sbt.Keys._
import sbt._

object CodacyCoveragePlugin extends AutoPlugin {

  object AutoImport {
    val codacyCoverage = taskKey[Unit]("Upload coverage reports to Codacy.")
    val codacyProjectToken = settingKey[Option[String]]("Your project token.")
    val coberturaFile = settingKey[File]("Path for project Cobertura file.")
    val codacyApiBaseUrl = settingKey[Option[String]]("The base URL for the Codacy API.")

    lazy val baseSettings: Seq[Def.Setting[_]] = Seq(
      codacyCoverage := {
        codacyCoverageCommand(state.value, baseDirectory.value, coberturaFile.value,
          crossTarget.value / "coverage-report" / "codacy-coverage.json",
          codacyProjectToken.value, codacyApiBaseUrl.value)
      },
      aggregate in codacyCoverage := false,
      codacyProjectToken := None,
      codacyApiBaseUrl := None,
      coberturaFile := crossTarget.value / ("coverage-report" + File.separator + "cobertura.xml")
    )
  }

  import com.codacy.CodacyCoveragePlugin.AutoImport._

  override def trigger: PluginTrigger = allRequirements

  override val projectSettings = baseSettings

  private val publicApiBaseUrl = "https://api.codacy.com"

  private def codacyCoverageCommand(state: State, rootProjectDir: File, coberturaFile: File, codacyCoverageFile: File,
                                    codacyToken: Option[String], codacyApiBaseUrl: Option[String]): Unit = {
    implicit val logger: Logger = state.log

    val commitUUID =
      sys.env.get("CI_COMMIT") orElse
      sys.env.get("TRAVIS_PULL_REQUEST_SHA") orElse
      sys.env.get("TRAVIS_COMMIT") orElse
      sys.env.get("DRONE_COMMIT") orElse
      sys.env.get("CIRCLE_SHA1") orElse
      sys.env.get("CI_COMMIT_ID") orElse
      sys.env.get("WERCKER_GIT_COMMIT")
        .filter(_.trim.nonEmpty)

    FileHelper.withTokenAndCommit(codacyToken, commitUUID) {
      case (projectToken, commitUUID) =>

        val reader = new CoberturaParser(Language.Scala, rootProjectDir, coberturaFile)
        val report = reader.generateReport()

        FileHelper.writeJsonToFile(codacyCoverageFile, report)

        val codacyClient = new CodacyClient(apiBaseUrl(codacyApiBaseUrl), projectToken = Some(projectToken))
        val coverageServices = new CoverageServices(codacyClient)

        logger.info(s"Uploading coverage data...")

        coverageServices.sendReport(commitUUID, Language.Scala, report) match {
          case requestResponse if requestResponse.hasError =>
            sys.error(s"Failed to upload data. Reason: ${requestResponse.message}")
            state.exit(ok = false)
            Left(requestResponse.message)
          case requestResponse =>
            logger.success(s"Coverage data uploaded. ${requestResponse.message}")
            Right(state)
        }
    } match {
      case Left(error) =>
        sys.error(error)
        state.exit(ok = false)
      case _ =>
    }
  }

  private def apiBaseUrl(codacyApiBaseUrl: Option[String]): Option[String] = {
    // Check for an environment variable to override the API URL.
    // If it doesn't exist, try the build options or default to the public API.
    sys.env.get("CODACY_API_BASE_URL")
      .orElse(codacyApiBaseUrl)
      .orElse(Some(publicApiBaseUrl))
  }

}
