package com.codacy

import java.io.File

import com.codacy.api.CodacyAPIClient
import com.codacy.io.FileUtils
import com.codacy.parsers.CoberturaParser
import com.codacy.vcs.GitClient
import play.api.libs.json.Json
import sbt.Keys._
import sbt._

import scala.util.Try

object CodacyCoveragePlugin extends AutoPlugin {

  object autoImport {
    val codacyCoverage = taskKey[Unit]("Upload coverage reports to Codacy.")
    val codacyProjectToken = settingKey[Option[String]]("Your project token.")
    val codacyProjectTokenFile = settingKey[Option[String]]("Path for file containing your project token.")
    val coberturaFile = settingKey[File]("Path for project Cobertura file.")

    lazy val baseSettings: Seq[Def.Setting[_]] = Seq(
      codacyCoverage := {
        codacyCoverageCommand(state.value, baseDirectory.value, coberturaFile.value,
          crossTarget.value / "coverage-report" / "codacy-coverage.json",
          codacyProjectToken.value, codacyProjectTokenFile.value)
      },
      codacyProjectToken := None,
      codacyProjectTokenFile := None,
      coberturaFile := crossTarget.value / ("coverage-report" + File.separator + "cobertura.xml")
    )
  }

  import com.codacy.CodacyCoveragePlugin.autoImport._

  //override def requires = ScoverageSbtPlugin

  override def trigger = allRequirements

  override val projectSettings = baseSettings

  private def codacyCoverageCommand(state: State, rootProjectDir: File, coberturaFile: File, codacyCoverageFile: File,
                                    codacyToken: Option[String], codacyTokenFile: Option[String]): Unit = {
    implicit val logger: Logger = state.log

    getProjectToken(codacyToken, codacyTokenFile).fold[State] {
      logger.error("Project token not defined.")
      state.exit(ok = false)
    } {
      projectToken =>

        Try {
          new GitClient(rootProjectDir).latestCommitUuid()
        }.toOption.fold[State] {
          logger.error("Could not get current commit.")
          state.exit(ok = false)
        } {
          commitUuid =>

            logger.info(s"Preparing coverage data for commit ${commitUuid.take(7)}...")

            FileUtils.get(coberturaFile).fold[State] {
              state.exit(ok = false)
            } {
              coberturaFile =>

                val reader = new CoberturaParser(coberturaFile, rootProjectDir)
                val report = reader.generateReport()
                FileUtils.write(codacyCoverageFile, Json.toJson(report).toString())

                logger.info(s"Uploading coverage data...")

                new CodacyAPIClient().postCoverageFile(projectToken, commitUuid, codacyCoverageFile).fold[State](
                  error => {
                    logger.error(s"Failed to upload data. Reason: $error")
                    state.exit(ok = false)
                  },
                  response => {
                    logger.success(s"Coverage data uploaded. $response")
                    state
                  })
            }
        }
    }
  }

  private def getProjectToken(codacyProjectToken: Option[String], codacyProjectTokenFile: Option[String]) = {
    sys.env.get("CODACY_PROJECT_TOKEN")
      .orElse(codacyProjectToken)
      .orElse(codacyProjectTokenFile.flatMap(FileUtils.read))
  }

}
