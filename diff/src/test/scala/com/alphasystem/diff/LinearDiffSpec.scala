package com.alphasystem.diff

import com.alphasystem.common.test._
import com.alphasystem.diff.OperationType._
import com.alphasystem.diff.greedy.GreedyDiff
import com.alphasystem.diff.linear.{Box, LinearDiff}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class LinearDiffSpec
  extends AnyFlatSpec
    with Matchers {

  System.setProperty(LogTimeConsumedPropertyName, "true")

  it should "find middle snake" in {
    val source = readLines("/example1.txt")
    val target = readLines("/example2.txt")
    val diff = LinearDiff(source, target)
    val maybeMiddleSnake = diff.findMiddleSnake(Box(4, 2, 6, 5))
    maybeMiddleSnake shouldBe defined
    val middleSnake = maybeMiddleSnake.get
    middleSnake.start shouldBe Point(5, 4)
    middleSnake.end shouldBe Point(6, 4)
  }

  it should "find paths" in {
    val source = readLines("/example1.txt")
    val target = readLines("/example2.txt")
    val diff = LinearDiff(source, target)
    val paths = diff.findPaths
    val expectedPaths = Point(0, 0) :: Point(1, 0) :: Point(2, 2) :: Point(3, 2) :: Point(4, 2) :: Point(5, 4) ::
      Point(6, 4) :: Point(6, 5) :: Point(9, 7) :: Point(10, 9) :: Point(11, 9) :: Point(11, 10) :: Point(11, 11) ::
      Point(12, 12) :: Point(13, 12) :: Point(13, 13) :: Point(14, 14) :: Nil
    paths shouldBe expectedPaths
  }

  it should "walk snake" in {
    val source = readLines("/example1.txt")
    val target = readLines("/example2.txt")
    val diff = LinearDiff(source, target)
    mapSnakeToPoints(diff.walkSnake(Point(0, 0), Point(1, 0))) shouldBe Point(0, 0) :: Nil
    mapSnakeToPoints(diff.walkSnake(Point(1, 0), Point(2, 2))) shouldBe Point(1, 0) :: Point(1, 1) :: Nil
    mapSnakeToPoints(diff.walkSnake(Point(6, 5), Point(9, 7))) shouldBe Point(6, 5) :: Point(7, 6) :: Point(8, 7) :: Nil
  }

  it should "walk path of snakes" in {
    val source = readLines("/example1.txt")
    val target = readLines("/example2.txt")
    val diff = LinearDiff(source, target)
    val paths = Point(0, 0) :: Point(1, 0) :: Point(2, 2) :: Point(3, 2) :: Nil
    val expectedResult = (Point(0, 0), Point(1, 0)) :: (Point(1, 0), Point(1, 1)) :: (Point(1, 1), Point(2, 2)) ::
      (Point(2, 2), Point(3, 2)) :: Nil
    diff.walkSnakes(paths).map(snake => (snake.start, snake.end)) shouldBe expectedResult
  }

  it should "find shortest edit path and longest common subsequence" in {
    val diff = LinearDiff("ABCABBA", "CBABAC")
    val expectedSnakes =
      Snake(Point(0, 0), Point(1, 0), Line(1, "A"), Deletion) ::
        Snake(Point(1, 0), Point(2, 0), Line(2, "B"), Deletion) ::
        Snake(Point(2, 0), Point(3, 1), Line(1, "C"), Matched) ::
        Snake(Point(3, 1), Point(4, 1), Line(4, "A"), Deletion) ::
        Snake(Point(4, 1), Point(5, 2), Line(2, "B"), Matched) ::
        Snake(Point(5, 2), Point(5, 3), Line(3, "A"), Insertion) ::
        Snake(Point(5, 3), Point(6, 4), Line(4, "B"), Matched) ::
        Snake(Point(6, 4), Point(7, 5), Line(5, "A"), Matched) ::
        Snake(Point(7, 5), Point(7, 6), Line(6, "C"), Insertion) :: Nil
    diff.shortestEditPath shouldBe expectedSnakes
    diff.lcs.map(_.line.text).mkString("") shouldBe "CBBA"
  }

  it should "find empty LCS for non-equal string" in {
    val result = LinearDiff("ABCDEF", "UVWXYX").lcs.map(_.line.text).mkString("")
    result shouldBe empty
  }

  it should "find same LCS with string with distinct elements with both algorithms" in {
    val s1 = "ABCDEFH"
    val s2 = "BCEGH"
    val lcs = "BCEH"
    GreedyDiff(s1, s2).lcs.map(_.line.text).mkString("") shouldBe lcs
    LinearDiff(s1, s2).lcs.map(_.line.text).mkString("") shouldBe lcs
  }

  it should "have valid solution" in {
    val source = readLines("/example1.txt")
    val target = readLines("/example2.txt")
    LinearDiff(source, target).validate shouldBe true
  }

  it should "perform full comparison in Map based diff" in {
    val source = readCsv("/example1.csv", CsvHeaders)
    val target = readCsv("/example3.csv", CsvHeaders)
    val diff = LinearDiff(source, target, CsvHeaders, CsvHeaders)
    diff.lcs.map(_.line.text("Last name")) shouldBe "Alfalfa" :: "Backus" :: "Bumpkin" :: "Franklin" :: "Gerty" :: Nil
  }

  it should "perform partial comparison in Map based diff" in {
    val source = readCsv("/example2.csv", CsvHeaders)
    val target = readCsv("/example3.csv", CsvHeaders)
    val diff = LinearDiff(source, target, CsvHeaders.take(1), CsvHeaders, CsvHeaders.take(1))
    diff.lcs.map {
      snake =>
        val map = snake.line.text
        s"${map("First name")} ${map("Last name")}"
    } shouldBe "Andrew Airpump" :: "Aloysius Alfalfa" :: "Benny Franklin" :: "Cecil Noshow" :: Nil
  }

  it should "Three way diff" in {
    val user1Source = Array(
      "1. celery",
      "2. salmon",
      "3. tomatoes",
      "4. garlic",
      "5. onions",
      "6. wine"
    )
    val user2Source = Array(
      "1. celery",
      "2. salmon",
      "3. garlic",
      "4. onions",
      "5. tomatoes",
      "6. wine"
    )
    val originalSource = Array(
      "1. celery",
      "2. garlic",
      "3. onions",
      "4. salmon",
      "5. tomatoes",
      "6. wine"
    )

    val diff1 = LinearDiff(originalSource, user1Source).shortestEditPath
    val diff2 = LinearDiff(originalSource, user2Source).shortestEditPath
    diff1.foreach(println)
    println("=" * 50)
    diff2.foreach(println)
  }

  private def mapSnakeToPoints[T](snakes: List[Snake[T]]): List[Point] = snakes.map(_.start)
}
