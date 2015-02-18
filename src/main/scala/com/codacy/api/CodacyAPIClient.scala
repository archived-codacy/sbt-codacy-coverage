package com.codacy.api

import java.io.File

import com.ning.http.client.AsyncHttpClient
import play.api.libs.ws.WSClient
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

class CodacyAPIClient {
  val client: WSClient = new NingWSClient(new AsyncHttpClient().getConfig)

  def postCoverageFile(projectToken: String, commitUuid: String, file: File): Either[String, String] = {
    //quick-fix load codacy instance from environment instead of only hardcoded.
    val host = sys.env.get("CODACY_INSTANCE_URL").getOrElse("https://www.codacy.com/")
    val url = s"$host/api/coverage/$projectToken/$commitUuid"

    val responseOpt = Try {
      val future = client.url(url).post(file)
      Await.result(future, Duration(10, SECONDS))
    }.toOption

    responseOpt.map {
      response =>
        Try {
          Right((response.json \ "success").as[String])
        }.toOption.getOrElse {
          Left(response.body)
        }
    }.getOrElse {
      Left("Could not connect to server.")
    }
  }

}
