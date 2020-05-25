package com.alphasystem.diff.linear

import com.alphasystem.diff._

import scala.util.control.Breaks._

/**
 * Diff with linear space refinement.
 *
 * Implementation is based on [[https://blog.jcoglan.com/2017/04/25/myers-diff-in-linear-space-implementation/]].
 *
 * @param source source array
 * @param target target array
 * @tparam T type of source and target objects
 */
abstract class LinearDiff[T] private[diff](private[diff] override val source: Array[Line[T]],
                                           private[diff] override val target: Array[Line[T]])
  extends Diff[T] {

  private[diff] override def findPaths: List[Point] = findPath(0, 0, source.length, target.length)

  private def findPath(left: Int,
                       top: Int,
                       right: Int,
                       bottom: Int): List[Point] = {
    val box = Box(left, top, right, bottom)
    val maybeMiddleSnake = findMiddleSnake(box)
    if (maybeMiddleSnake.isDefined) {
      val middleSnake = maybeMiddleSnake.get
      val start = middleSnake.start
      val end = middleSnake.end

      val forwards = findPath(box.left, box.top, start.x, start.y)
      val reverses = findPath(end.x, end.y, box.right, box.bottom)

      val ls1 =
        forwards match {
          case Nil => start :: Nil
          case _ => forwards
        }

      val ls2 =
        reverses match {
          case Nil => end :: Nil
          case _ => reverses
        }

      ls1 ::: ls2
    } else Nil
  }

  private[diff] def findMiddleSnake(box: Box): Option[MiddleSnake] = {
    var result: Option[MiddleSnake] = None
    if (box.size > 0) {
      val max = (box.size / 2.0).ceil.toInt

      var forwardV = Map.empty[Int, Int].withDefaultValue(0)
      var reverseV = Map.empty[Int, Int].withDefaultValue(0)
      forwardV += (1 -> box.left)
      reverseV += (1 -> box.bottom)

      breakable {
        for (d <- 0 to max) {
          for (k <- (-d to d by 2).reverse) {
            val c = k - box.delta
            val down = k == -d || (k != -d && forwardV(k - 1) < forwardV(k + 1))

            val xStart = if (down) forwardV(k + 1) else forwardV(k - 1)
            var xEnd = if (down) xStart else xStart + 1
            var yEnd = box.top + (xEnd - box.left) - k
            val yStart = if (d == 0 || xStart != xEnd) yEnd else yEnd - 1

            while (xEnd < box.right && yEnd < box.bottom && compareLines(xEnd, yEnd)) {
              xEnd += 1
              yEnd += 1
            }

            log.debug("Forward: Box = {}, xStart = {}, yStart = {}, xEnd = {}, yEnd = {}",
              box, xStart, yStart, xEnd, yEnd)
            forwardV += (k -> xEnd)

            if (!box.evenDelta && c >= -(d - 1) && c <= (d - 1) && yEnd >= reverseV(c)) {
              result = Some(MiddleSnake(xStart, yStart, xEnd, yEnd))
              break()
            }
          } // end of forward for loop

          for (c <- (-d to d by 2).reverse) {
            val k = c + box.delta
            val up = c == -d || (c != d && reverseV(c - 1) > reverseV(c + 1))

            val yStart = if (up) reverseV(c + 1) else reverseV(c - 1)
            var yEnd = if (up) yStart else yStart - 1
            var xEnd = box.left + (yEnd - box.top) + k
            val xStart = if (d == 0 || yEnd != yStart) xEnd else xEnd + 1

            while (xEnd > box.left & yEnd > box.top && compareLines(xEnd - 1, yEnd - 1)) {
              xEnd -= 1
              yEnd -= 1
            }

            log.debug(s"Reverse: Box = {}, xStart = {}, yStart = {}, xEnd = {}, yEnd = {}", box, xStart, yStart, xEnd, yEnd)
            reverseV += (c -> yEnd)

            if (box.evenDelta && k >= -d && k <= d && xEnd <= forwardV(k)) {
              result = Some(MiddleSnake(xEnd, yEnd, xStart, yStart))
              break()
            }

          } // end of reverse for loop

          if (result.isDefined) {
            break()
          }
        } // end of for loop
      } // end of breakable
    }
    log.debug("Middle snake = {}", result)
    result
  }

  private[diff] override def walkSnakes(paths: List[Point]): List[Snake[T]] =
    paths
      .sliding(2)
      .foldLeft(List[Snake[T]]()) {
        (result, input) => result ::: walkSnake(input.head, input.last)
      }

  private[diff] def walkSnake(start: Point, end: Point): List[Snake[T]] = {
    // start and end point could be a single rightward (for forward direction) step or a single leftward (for reverse
    // direction), in either case start.x + 1 == end.x and start.y == end.y, this indicate deletion

    // start and end point could be a single downward (for forward direction) step or a single upward (for reverse
    // direction), in either case start.x == end.x and start.y + 1 == end.y, this indicates insertion

    // There are two cases to find diagonal

    // CASE 1
    // a snake is composed of a single rightward and downward step followed by zero or more diagonal ones. Because of
    // snakes found by scanning backward, they can also be a sequence of diagonal steps followed by a single rightward
    // or downward one (leftward or upward in reverse direction).

    // CASE 2
    // As well as deciding which direction this step is in, we also need to decide whether it appears at the beginning
    // or the end of the snake, that is, do we have the sequence on the left in the above figure, or the one on the
    // right? To figure this out we can compare the lines at the starting point of the snake. If line 6 in the old
    // version is equal to line 5 in the new one, then the first step is a diagonal and the rightward step comes at
    // the end. Otherwise, it comes at the start.

    // in order to determine whether rightward or downward, take the difference of x and y of start and end, whichever
    // has higher value we would make that move until xDiff and yDiff
    // For example, consider points [6, 5] and [9, 7], the difference of  9 and 6 (3) is greater then the difference of
    // 7 and 5 (2), so we should make rightward move first then go diagonally

    var result = walkDiagonal(start, end)
    val last = result.last
    var x = last.x
    var y = last.y
    val diff = (end.x - x) - (end.y - y)
    val singleUpOrDownStep = (x + 1 == end.x && y == end.y) || (x == end.x && y + 1 == end.y)
    if (singleUpOrDownStep) {
      // deletion or insertion
      // do nothing we already have the result
    } else if (diff == -1) {
      // CASE 2 of diagonal, indicates must make downward move (insertion) first before go to diagonal
      y += 1
    } else if (diff == 1) {
      // CASE 2 of diagonal, indicates must make rightward move (deletion) first before go to diagonal
      x += 1
    } else {
      // CASE 1 diagonal move
      x += 1
      y += 1
    }

    result = if (singleUpOrDownStep) result else result ::: walkDiagonal(Point(x, y), end)
    (result :+ end)
      .sliding(2)
      .foldLeft(List.empty[Snake[T]]) {
        (result, input) =>
          val start = input.head
          val end = input.last
          result :+ createSnake(start, end, source, target)
      }
  }

  private def walkDiagonal(start: Point, end: Point): List[Point] = {
    var ls = List.empty[Point]
    var x = start.x
    var y = start.y
    while (x < end.x && y < end.y && compareLines(x, y)) {
      ls :+= Point(x, y)
      x += 1
      y += 1
    }
    val point = Point(x, y)
    if (point != end) ls :+= point // do not include the end, it will be part of next iteration
    ls
  }
}

private class DefaultLinearDiff(source: Array[Line[String]],
                                target: Array[Line[String]])
  extends LinearDiff[String](source, target)

private class MapBasedLinearDiff(source: Array[Line[Map[String, String]]],
                                 target: Array[Line[Map[String, String]]],
                                 override private[diff] val sourceHeaders: List[String] = Nil,
                                 override private[diff] val targetHeaders: List[String] = Nil,
                                 override private[diff] val headersToCompare: List[String] = Nil)
  extends LinearDiff[Map[String, String]](source, target)
    with MapBasedDiff

object LinearDiff {
  def apply(source: Array[Line[String]], target: Array[Line[String]]): LinearDiff[String] =
    new DefaultLinearDiff(source, target)

  def apply(source: Array[String], target: Array[String]): LinearDiff[String] =
    new DefaultLinearDiff(toLine(source), toLine(target))

  def apply(source: String, target: String): LinearDiff[String] =
    new DefaultLinearDiff(toStringArray(source), toStringArray(target))

  def apply(source: Array[Line[Map[String, String]]],
            target: Array[Line[Map[String, String]]],
            sourceHeaders: List[String] = Nil,
            targetHeaders: List[String] = Nil,
            headersToCompare: List[String] = Nil): LinearDiff[Map[String, String]] =
    new MapBasedLinearDiff(source, target, sourceHeaders, targetHeaders, headersToCompare)
}
