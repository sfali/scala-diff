package com.alphasystem.diff

import java.time.Duration

import org.slf4j.{Logger, LoggerFactory}

trait Diff[T] {

  protected val log: Logger = LoggerFactory.getLogger(getClass)
  private val logTimeConsumed: Boolean = java.lang.Boolean.getBoolean(LogTimeConsumedPropertyName)
  private[diff] val source: Array[Line[T]]
  private[diff] val target: Array[Line[T]]
  private var snakes: List[Snake[T]] = List.empty[Snake[T]]

  private[diff] def findPaths: List[Point]

  private[diff] def walkSnakes(paths: List[Point]): List[Snake[T]]

  private[diff] def compareLines(sourceIndex: Int, targetIndex: Int): Boolean =
    source(sourceIndex).text == target(targetIndex).text

  /**
   * A list of [[Snake]]s containing the shortest edit path.
   *
   * @return A list of [[Snake]]s containing the shortest edit path
   */
  def shortestEditPath: List[Snake[T]] = {
    time("FindPaths") {
      val paths = findPaths
      time("WalkSnakes") {
        snakes = walkSnakes(paths)
      }
    }
    snakes
  }

  def lcs: List[Snake[T]] = {
    if (snakes.isEmpty) shortestEditPath
    time("Filter LCS") {
      snakes.filter(_.operationType == OperationType.Matched)
    }
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

  private def validate(snake: Snake[T]): Boolean = {
    val start = snake.start
    val end = snake.end
    val right = start.x + 1 == end.x && start.y == end.y
    val down = start.x == end.x && start.y + 1 == end.y
    val diagonal = start.x + 1 == end.x && start.y + 1 == end.y
    right || down || diagonal
  }

  private def time[R](description: String)(block: => R): R = {
    if (logTimeConsumed) log.info("Starting step '{}'", description)
    val start = System.nanoTime()
    val result = block
    val timeElapsed = Duration.ofNanos(System.nanoTime() - start)
    if (logTimeConsumed) {
      log.info("{}: time taken to run step '{}': {}", getClass.getSimpleName, description, timeElapsed)
      log.info("Done step '{}'", description)
    }
    result
  }

}
