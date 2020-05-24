package com.alphasystem.diff

import org.slf4j.{Logger, LoggerFactory}

trait Diff {

  protected val log: Logger = LoggerFactory.getLogger(getClass)

  private var snakes: List[Snake] = List.empty[Snake]

  private[diff] def walkSnakes: List[Snake]

  def shortestEditPath: List[Snake] = {
    snakes = walkSnakes
    snakes
  }

  def lcs: List[Snake] = {
    if (snakes.isEmpty) shortestEditPath
    snakes.filter(_.operationType == OperationType.Matched)
  }

}
