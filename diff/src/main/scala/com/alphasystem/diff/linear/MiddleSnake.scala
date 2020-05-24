package com.alphasystem.diff.linear

import com.alphasystem.diff.Point

case class MiddleSnake(start: Point, end: Point)

object MiddleSnake {
  def apply(start: Point, end: Point): MiddleSnake = new MiddleSnake(start, end)

  def apply(xStart: Int, yStart: Int, xEnd: Int, yEnd: Int): MiddleSnake =
    MiddleSnake(Point(xStart, yStart), Point(xEnd, yEnd))
}
