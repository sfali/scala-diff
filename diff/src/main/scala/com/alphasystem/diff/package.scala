package com.alphasystem

package object diff {

  val NewLine: String = System.lineSeparator()
  val UserHome: String = System.getProperty("user.home")

  def toLine(source: Array[String]): Array[Line[String]] =
    source.zipWithIndex.map {
      case (str, i) => Line(i + 1, str)
    }

  def toStringArray(s: String): Array[Line[String]] = toLine(s.map(_.toString).toArray)

  def createSnake[T](start: Point,
                     end: Point,
                     source: Array[Line[T]],
                     target: Array[Line[T]]): Snake[T] = {
    val operationType = OperationType.toOperationType(start, end)
    Snake(start, end, getLine(source, target, end, operationType), operationType)
  }

  private def getLine[T](source: Array[Line[T]],
                         target: Array[Line[T]],
                         end: Point,
                         operationType: OperationType): Line[T] =
    operationType match {
      case OperationType.Insertion => target(end.y - 1)
      case OperationType.Deletion => source(end.x - 1)
      case OperationType.Matched => target(end.y - 1)
      case OperationType.None => source(0)
    }

}
