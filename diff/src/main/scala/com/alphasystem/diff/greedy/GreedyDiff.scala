package com.alphasystem.diff.greedy

import com.alphasystem.diff._

import scala.util.control.Breaks._

class GreedyDiff(source: Array[Line], target: Array[Line]) extends Diff {

  override private[diff] def walkSnakes = {
    var snakes = List.empty[Snake]
    val n = source.length
    val m = target.length
    val max = n + m

    // map holding -max to max "x" values, each key of this map is "k"
    var v = Map.empty[Int, Int].withDefaultValue(0)

    var result = max
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

          while (xEnd < n && yEnd < m && source(xEnd).text == target(yEnd).text) {
            val maybeSnake = createSnake(xStart, yStart, xEnd, yEnd, source, target)
            if (maybeSnake.isDefined) {
              snakes +:= maybeSnake.get
            }
            xStart = xEnd
            yStart = yEnd
            xEnd += 1
            yEnd += 1
          }
          // best value for k
          v += (k -> xEnd)
          val maybeSnake = createSnake(xStart, yStart, xEnd, yEnd, source, target)
          if (maybeSnake.isDefined) {
            snakes +:= maybeSnake.get
          }
          if (xEnd >= n && yEnd >= m) {
            result = d
            break()
          }

        } // end of inner for loop
      } // end of outer for loop
    } // end of breakable
    backtrack(snakes, Nil, None)
  }

  /*
 * Results are backward, start from bottom and go up
 */
  @scala.annotation.tailrec
  private def backtrack(source: List[Snake], result: List[Snake], maybePrev: Option[Snake]): List[Snake] =
    source match {
      case Nil => result
      case head :: tail =>
        maybePrev match {
          case None =>
            backtrack(tail, head +: result, Some(head))
          case Some(prev) =>
            // fictitious (0, -1) ignore
            if (head.start.y == -1 || prev.start != head.end) {
              backtrack(tail, result, maybePrev)
            } else {
              // one of our path
              backtrack(tail, head +: result, Some(head))
            }
        }
    }

}

object GreedyDiff {
  def apply(source: Array[Line], target: Array[Line]): GreedyDiff = new GreedyDiff(source, target)

  def apply(source: Array[String], target: Array[String]): GreedyDiff = new GreedyDiff(toLine(source), toLine(target))

  def apply(source: String, target: String): GreedyDiff = new GreedyDiff(toStringArray(source), toStringArray(target))
}
