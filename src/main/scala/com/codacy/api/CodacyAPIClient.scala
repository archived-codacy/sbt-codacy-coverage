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
    val url = s"https://www.codacy.com/api/coverage/$projectToken/$commitUuid"

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
