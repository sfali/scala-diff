package com.alphasystem.diff.cli

import java.nio.file.Path

case class Config(sourceFile: Option[Path] = None,
                  targetFile: Option[Path] = None,
                  outFile: Option[Path] = None,
                  operation: Option[Operation] = None,
                  sourceHeaders: List[String] = Nil,
                  targetHeaders: List[String] = Nil,
                  headersToCompare: List[String] = Nil,
                  logTimeConsumed: Option[Boolean] = None,
                  maxLineLength: Option[Int] = None,
                  lineSeparator: Option[String] = None)
