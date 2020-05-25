package com.alphasystem.diff

trait MapBasedDiff extends Diff[Map[String, String]] {

  private[diff] val sourceHeaders: List[String] = Nil
  private[diff] val targetHeaders: List[String] = Nil
  private[diff] val headersToCompare: List[String] = Nil

  private lazy val _headersToCompare: List[String] = headersToCompare.intersect(sourceHeaders).intersect(targetHeaders)

  private lazy val partialComparison = _headersToCompare.nonEmpty

  override private[diff] def compareLines(sourceIndex: Int, targetIndex: Int) =
    if (partialComparison) {
      val sourceMap = source(sourceIndex).text
      val targetMap = target(targetIndex).text
      _headersToCompare.map(key => sourceMap(key)) == _headersToCompare.map(key => targetMap(key))
    } else super.compareLines(sourceIndex, targetIndex)

}
