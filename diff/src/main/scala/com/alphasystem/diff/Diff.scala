package com.alphasystem.diff

import org.slf4j.{Logger, LoggerFactory}

trait Diff {

  protected val log: Logger = LoggerFactory.getLogger(getClass)

  private var snakes: List[Snake] = List.empty[Snake]

  private[diff] def walkSnakes: List[Snake]

  /**
   * A list of [[Snake]]s containing the shortest edit path.
   *
   * @return A list of [[Snake]]s containing the shortest edit path
   */
  def shortestEditPath: List[Snake] = {
    snakes = walkSnakes
    snakes
  }

  def lcs: List[Snake] = {
    if (snakes.isEmpty) shortestEditPath
    snakes.filter(_.operationType == OperationType.Matched)
  }

  /**
   * Determine the validity of the solution.
   *
   * A solution can only be valid if one of the following condition is true:
   *
   * #
   * #
   * #
   *
   * @return true if the given solution is valid, false otherwise.
   */
  def validate: Boolean = {
    if (snakes.isEmpty) shortestEditPath
    snakes.forall(validate)
  }

  private def validate(snake: Snake): Boolean = {
    val start = snake.start
    val end = snake.end
    val right = start.x + 1 == end.x && start.y == end.y
    val down = start.x == end.x && start.y + 1 == end.y
    val diagonal = start.x + 1 == end.x && start.y + 1 == end.y
    right || down || diagonal
  }

}
