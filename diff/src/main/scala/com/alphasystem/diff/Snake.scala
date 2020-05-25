package com.alphasystem.diff

case class Snake[T](start: Point,
                 end: Point,
                 line: Line[T],
                 operationType: OperationType)
