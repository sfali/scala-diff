package com.alphasystem

import scala.util.{Failure, Success, Try}

package object diff {

  val NewLine: String = System.lineSeparator()
  val UserHome: String = System.getProperty("user.home")

  def toLine(source: Array[String]): Array[Line] =
    source.zipWithIndex.map {
      case (str, i) => Line(i + 1, str)
    }

  def toStringArray(s: String): Array[Line] = toLine(s.map(_.toString).toArray)

  def createSnake(xStart: Int,
                  yStart: Int,
                  xEnd: Int,
                  yEnd: Int,
                  source: Array[Line],
                  target: Array[Line]): Option[Snake] = {
    val start = Point(xStart, yStart)
    val end = Point(xEnd, yEnd)
    val operationType = OperationType.toOperationType(start, end)
    getLine(source, target, end, operationType) match {
      case Some(line) => Some(Snake(start, end, line, operationType))
      case None => None
    }
  }

  private def getLine(source: Array[Line],
                      target: Array[Line],
                      end: Point,
                      operationType: OperationType): Option[Line] =
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
