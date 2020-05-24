package com.alphasystem.diff

import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait OperationType extends EnumEntry

object OperationType extends Enum[OperationType] with CirceEnum[OperationType] {
  override def values: IndexedSeq[OperationType] = findValues

  final case object Insertion extends OperationType

  final case object Deletion extends OperationType

  final case object Matched extends OperationType

  final case object None extends OperationType

  def toOperationType(start: Point, end: Point): OperationType =
    if (start.y == -1) OperationType.None
    else if (start.x + 1 == end.x && start.y == end.y) OperationType.Deletion
    else if (start.x == end.x && start.y + 1 == end.y) OperationType.Insertion
    else if (start.x + 1 == end.x && start.y + 1 == end.y) OperationType.Matched
    else OperationType.None

}
