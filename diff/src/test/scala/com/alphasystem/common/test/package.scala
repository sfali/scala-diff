package com.alphasystem.common

import java.nio.file.{Files, Paths}

import com.alphasystem.diff.Line

import scala.jdk.CollectionConverters._

package object test {

  val CsvHeaders: List[String] = "Last name" :: "First name" :: "SSN" :: "Test1" :: "Test2" :: "Test3" :: "Test4" ::
    "Final" :: "Grade" :: Nil

  def readLines(resourceName: String): Array[String] =
    Files.readAllLines(Paths.get(getClass.getResource(resourceName).toURI)).asScala.toArray

  def readCsv(resourceName: String, headers: List[String]): Array[Line[Map[String, String]]] =
    Files.readAllLines(Paths.get(getClass.getResource(resourceName).toURI)).asScala.toArray
      .drop(1)
      .map {
        line =>
          val values = line.split(",")
          headers.zip(values)
            .map {
              case (key, value) => key -> value
            }.toMap
      }.zipWithIndex
      .map {
        case (value, i) => Line(i + 1, value)
      }

}
