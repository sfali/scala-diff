package com.alphasystem.diff

import java.nio.charset.StandardCharsets
import java.nio.file.Path

import akka.NotUsed
import akka.stream.IOResult
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.{FileIO, Flow, Framing, Source}
import akka.util.ByteString

import scala.concurrent.Future

package object client {

  val MaxLineLengthPropertyName: String = "scala-diff.max-line-length"
  private val MaxLineLength = java.lang.Integer.getInteger(MaxLineLengthPropertyName, 1024).toInt

  val LineSeparatorPropertyName: String = "scala-diff.line-separator"
  private val LineSeparator = ByteString(System.getProperty(LineSeparatorPropertyName, System.lineSeparator()))

  val LineSplitter: Flow[ByteString, ByteString, NotUsed] =
    Framing.delimiter(LineSeparator, maximumFrameLength = MaxLineLength, allowTruncation = true)

  def fileReader(path: Path): Source[Line[String], Future[IOResult]] =
    FileIO
      .fromPath(path)
      .via(LineSplitter)
      .map(_.utf8String)
      .zipWithIndex
      .map {
        case (str, index) => Line(index + 1, str)
      }

  def csvReader(path: Path, headers: Seq[String]): Source[Line[Map[String, String]], Future[IOResult]] =
    FileIO
      .fromPath(path)
      .via(CsvParsing.lineScanner())
      .via(CsvToMap.withHeadersAsStrings(StandardCharsets.UTF_8, headers: _*))
      .drop(1) // drop header line
      .zipWithIndex
      .map {
        case (map, index) => Line(index + 1, map)
      }
}
