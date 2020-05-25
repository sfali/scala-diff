package com.alphasystem.diff

import com.alphasystem.common.test._
import com.alphasystem.diff.OperationType._
import com.alphasystem.diff.greedy.GreedyDiff
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GreedyDiffSpec
  extends AnyFlatSpec
    with Matchers {

  System.setProperty("scala-diff.log-time-consumed", "true")
  
  it should "find shortest edit script" in {
    val result = GreedyDiff("ABCABBA", "CBABAC").shortestEditPath

    val expectedResult =
      Snake(Point(0, 0), Point(1, 0), Line(1, "A"), Deletion) ::
        Snake(Point(1, 0), Point(2, 0), Line(2, "B"), Deletion) ::
        Snake(Point(2, 0), Point(3, 1), Line(1, "C"), Matched) ::
        Snake(Point(3, 1), Point(3, 2), Line(2, "B"), Insertion) ::
        Snake(Point(3, 2), Point(4, 3), Line(3, "A"), Matched) ::
        Snake(Point(4, 3), Point(5, 4), Line(4, "B"), Matched) ::
        Snake(Point(5, 4), Point(6, 4), Line(6, "B"), Deletion) ::
        Snake(Point(6, 4), Point(7, 5), Line(5, "A"), Matched) ::
        Snake(Point(7, 5), Point(7, 6), Line(6, "C"), Insertion) :: Nil

    result shouldBe expectedResult
  }

  it should "find longest common subsequence" in {
    val result = GreedyDiff("ABCABBA", "CBABAC").lcs.map(_.line.text).mkString("")
    result shouldBe "CABA"
  }

  it should "find empty LCS for non-equal string" in {
    val result = GreedyDiff("ABCDEF", "UVWXYX").lcs.map(_.line.text).mkString("")
    result shouldBe empty
  }

  it should "compare two files" in {
    val source = readLines("/file1.scala")
    val target = readLines("/file2.scala")
    val result = GreedyDiff(source, target).shortestEditPath
      .map {
        snake => (snake.line.lineNumber, snake.operationType)
      }
    val expectedResult = (1, Matched) :: (2, Matched) :: (3, Matched) :: (4, Matched) :: (5, Matched) ::
      (6, Matched) :: (7, Insertion) :: (8, Insertion) :: (9, Insertion) :: (10, Insertion) :: (11, Matched) :: Nil
    result shouldBe expectedResult
  }

  it should "perform full comparison in Map based diff" in {
    val source = readCsv("/example1.csv", CsvHeaders)
    val target = readCsv("/example3.csv", CsvHeaders)
    val diff = GreedyDiff(source, target, CsvHeaders, CsvHeaders)
    diff.lcs.map(_.line.text("Last name")) shouldBe "Alfalfa" :: "Backus" :: "Bumpkin" :: "Franklin" :: "Gerty" :: Nil
  }

  it should "perform partial comparison in Map based diff" in {
    val source = readCsv("/example2.csv", CsvHeaders)
    val target = readCsv("/example3.csv", CsvHeaders)
    val diff = GreedyDiff(source, target, CsvHeaders.take(1), CsvHeaders, CsvHeaders.take(1))
    diff.lcs.map {
      snake =>
        val map = snake.line.text
        s"${map("First name")} ${map("Last name")}"
    } shouldBe "Andrew Airpump" :: "Aloysius Alfalfa" :: "Benny Franklin" :: "Cecil Noshow" :: Nil
  }
}
