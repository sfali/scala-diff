package com.alphasystem.diff.cli

import java.io.File

import akka.actor.ActorSystem
import com.alphasystem.diff._
import com.alphasystem.diff.client._
import scopt.{OParser, Read}

object Main {

  implicit val OperationRead: Read[Operation] = scopt.Read.reads(s => Operation.withName(s.toUpperCase))

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("scala-diff")

    OParser.parse(readArgs(args), args, Config()) match {
      case Some(config) =>
        // execute
        if (config.logTimeConsumed.isDefined) {
          System.setProperty(LogTimeConsumedPropertyName, config.logTimeConsumed.get.toString)
        }
        if (config.maxLineLength.isDefined) {
          System.setProperty(MaxLineLengthPropertyName, config.maxLineLength.get.toString)
        }
        if (config.lineSeparator.isDefined) {
          System.setProperty(LineSeparatorPropertyName, config.lineSeparator.get)
        }
        system.terminate() // TODO: remove this, once have a working code

      case _ =>
        // arguments are bad, error message already have been displayed
        system.terminate()
    }
  }

  private def readArgs(args: Array[String]): OParser[Unit, Config] = {
    val builder = OParser.builder[Config]
    import builder._
    OParser
      .sequence(
        programName("ScalaDiff"),
        opt[File]('s', "source-file")
          .required()
          .action((x, c) => c.copy(sourceFile = Some(x.toPath)))
          .text("Source file name."),
        opt[File]('t', "target-file")
          .required()
          .action((x, c) => c.copy(targetFile = Some(x.toPath)))
          .text("Target file name"),
        opt[File]('o', "out-file")
          .required()
          .action((x, c) => c.copy(targetFile = Some(x.toPath)))
          .text("Output file name."),
        opt[Unit]('l', "log-time-consumed")
          .optional()
          .action((_, c) => c.copy(logTimeConsumed = Some(true)))
          .text("Log execution time consumption."),
        opt[Int]("max-line-length")
          .optional()
          .abbr("ll")
          .action((x, c) => c.copy(maxLineLength = Some(x)))
          .text("Maximum line length of each line, this property is used by parser to parse each line, default value is 1024."),
        opt[String]("line-separator")
          .optional()
          .abbr("ls")
          .action((x, c) => c.copy(lineSeparator = Some(x)))
          .text("LineSeparator, default value is system line separator."),
        opt[Seq[String]]("source-headers")
          .optional()
          .abbr("sh")
          .valueName("header1,header2...")
          .action((x, c) => c.copy(sourceHeaders = x.toList))
          .text("List of source headers, only applicable if source file is CSV."),
        opt[Seq[String]]("target-headers")
          .optional()
          .abbr("th")
          .valueName("header1,header2...")
          .action((x, c) => c.copy(targetHeaders = x.toList))
          .text("List of target headers, only applicable if target file is CSV."),
        opt[Seq[String]]("headers-to-compare")
          .optional()
          .abbr("hc")
          .valueName("header1,header2...")
          .action((x, c) => c.copy(headersToCompare = x.toList))
          .text("List of headers to compare, only applicable source and target files are CSV."),
        cmd("ses")
          .action((_, c) => c.copy(operation = Some(Operation.SES))),
        cmd("lcs")
          .action((_, c) => c.copy(operation = Some(Operation.LCS))),
        checkConfig(c =>
          if (c.operation.isEmpty) failure("Missing operation, must provide either ses | lcs.")
          else success)

      )
  }
}
