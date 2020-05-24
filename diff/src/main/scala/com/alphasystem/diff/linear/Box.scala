package com.alphasystem.diff.linear

case class Box(left: Int, top: Int, right: Int, bottom: Int) {
  val width: Int = right - left

  val height: Int = bottom - top

  val size: Int = width + height

  val delta: Int = width - height

  val evenDelta: Boolean = delta % 2 == 0
}
