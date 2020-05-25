package com.alphasystem

import scala.util.{Failure, Success, Try}

package object diff {

  val NewLine: String = System.lineSeparator()
  val UserHome: String = System.getProperty("user.home")

  def toLine(source: Array[String]): Array[Line[String]] =
    source.zipWithIndex.map {
      case (str, i) => Line(i + 1, str)
    }

  def toStringArray(s: String): Array[Line[String]] = toLine(s.map(_.toString).toArray)

  def createSnake[T](xStart: Int,
                     yStart: Int,
                     xEnd: Int,
                     yEnd: Int,
                     source: Array[Line[T]],
                     target: Array[Line[T]]): Option[Snake[T]] = {
    val start = Point(xStart, yStart)
    val end = Point(xEnd, yEnd)
    val operationType = OperationType.toOperationType(start, end)
    getLine(source, target, end, operationType) match {
      case Some(line) => Some(Snake(start, end, line, operationType))
      case None => None
    }
  }

  private def getLine[T](source: Array[Line[T]],
                         target: Array[Line[T]],
                         end: Point,
                         operationType: OperationType): Option[Line[T]] =
    Try {
      operationType match {
        case OperationType.Insertion => target(end.y - 1)
        case OperationType.Deletion => source(end.x - 1)
        case OperationType.Matched => target(end.y - 1)
        case OperationType.None => source(0)
      }
    } match {
      case Failure(_) => None
      case Success(line) => Some(line)
    }

}
