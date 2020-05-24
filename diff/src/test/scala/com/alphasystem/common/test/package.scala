package com.alphasystem.common

import java.nio.file.{Files, Paths}

import scala.jdk.CollectionConverters._

package object test {

  def readLines(resourseName: String): Array[String] =
    Files.readAllLines(Paths.get(getClass.getResource(resourseName).toURI)).asScala.toArray
}
