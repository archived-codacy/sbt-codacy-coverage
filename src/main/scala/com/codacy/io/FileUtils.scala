package com.codacy.io

import java.io.{File, PrintWriter}

import scala.io.Source
import scala.util.Try

object FileUtils {

  def get(file: File): Option[File] = {
    file match {
      case f if f.exists() => Some(f)
      case f => None
    }
  }

  def read(filePath: String): Option[String] = {
    Try {
      val source = Source.fromFile(filePath)
      val repoToken = source.mkString.trim
      source.close()
      repoToken
    }.toOption
  }

  def write(filePath: File, content: String): Unit = {
    Try {
      val printer = new PrintWriter(filePath)
      printer.println(content)
      printer.close()
    }.toOption
  }

}