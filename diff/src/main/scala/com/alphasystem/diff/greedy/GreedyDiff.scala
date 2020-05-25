package com.alphasystem.diff.greedy

import com.alphasystem.diff._

import scala.util.control.Breaks._

abstract class GreedyDiff[T] private[diff](private[diff] override val source: Array[Line[T]],
                                           private[diff] override val target: Array[Line[T]])
  extends Diff[T] {

  private[diff] override def findPaths: List[Point] = {
    var paths: List[Point] = Nil
    val n = source.length
    val m = target.length
    val max = n + m

    // map holding -max to max "x" values, each key of this map is "k"
    var v = Map.empty[Int, Int].withDefaultValue(0)

    breakable {
      for (d <- 0 to max) {
        for (k <- -d to d by 2) {
          val down = k == -d || (k != -d && v(k - 1) < v(k + 1))

          // initial x and y
          var xStart = if (down) v(k + 1) else v(k - 1)
          var yStart = xStart - (if (down) k + 1 else k - 1)

          // x and y end coordinates
          var xEnd = if (down) xStart else xStart + 1
          var yEnd = xEnd - k

          while (xEnd < n && yEnd < m && compareLines(xEnd, yEnd)) {
            paths +:= Point(xStart, yStart)
            paths +:= Point(xEnd, yEnd)
            xStart = xEnd
            yStart = yEnd
            xEnd += 1
            yEnd += 1
          }
          // best value for k
          v += (k -> xEnd)
          paths +:= Point(xStart, yStart)
          paths +:= Point(xEnd, yEnd)
          if (xEnd >= n && yEnd >= m) {
            break()
          }

        } // end of inner for loop
      } // end of outer for loop
    } // end of breakable
    paths
  }

  private[diff] override def walkSnakes(paths: List[Point]): List[Snake[T]] =
    walkSnakesInternal(paths.sliding(2, 2).toList, Nil, None)

  @scala.annotation.tailrec
  private def walkSnakesInternal(paths: List[List[Point]],
                                 result: List[Snake[T]],
                                 maybePrev: Option[Snake[T]]): List[Snake[T]] = {
    paths match {
      case Nil => result
      case first :: tail =>
        maybePrev match {
          case None =>
            val start = first.last
            val end = first.head
            val snake = createSnake(start, end, source, target)
            walkSnakesInternal(tail, snake +: result, Some(snake))

          case Some(prev) =>
            val start = first.last
            val end = first.head
            // fictitious (0, -1) ignore
            if (start.y == -1 || prev.start != end) walkSnakesInternal(tail, result, maybePrev)
            else {
              // one of our path
              val snake = createSnake(start, end, source, target)
              walkSnakesInternal(tail, snake +: result, Some(snake))
            }
        }
    }
  }
}

private class DefaultGreedyDiff(source: Array[Line[String]], target: Array[Line[String]])
  extends GreedyDiff[String](source, target)

private class MapBasedGreedyDiff(source: Array[Line[Map[String, String]]],
                                 target: Array[Line[Map[String, String]]],
                                 override private[diff] val sourceHeaders: List[String] = Nil,
                                 override private[diff] val targetHeaders: List[String] = Nil,
                                 override private[diff] val headersToCompare: List[String] = Nil)
  extends GreedyDiff[Map[String, String]](source, target)
    with MapBasedDiff

object GreedyDiff {
  def apply(source: Array[Line[String]], target: Array[Line[String]]): GreedyDiff[String] =
    new DefaultGreedyDiff(source, target)

  def apply(source: Array[String], target: Array[String]): GreedyDiff[String] =
    new DefaultGreedyDiff(toLine(source), toLine(target))

  def apply(source: String, target: String): GreedyDiff[String] =
    new DefaultGreedyDiff(toStringArray(source), toStringArray(target))

  def apply(source: Array[Line[Map[String, String]]],
            target: Array[Line[Map[String, String]]],
            sourceHeaders: List[String] = Nil,
            targetHeaders: List[String] = Nil,
            headersToCompare: List[String] = Nil): GreedyDiff[Map[String, String]] =
    new MapBasedGreedyDiff(source, target, sourceHeaders, targetHeaders, headersToCompare)
}
