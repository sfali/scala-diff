package com.alphasystem.diff.cli

import com.alphasystem.diff.linear.LinearDiff
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import java.nio.file.Paths

class DiffViewerTest
  extends AnyFlatSpec
    with Matchers {

  it should "generate unified viewer" in {
    val fileName = "NewDocumentDialog.java"
    val baseDir = "cli/test"
    val source = Paths.get(".", baseDir, "v2", fileName).toAbsolutePath
    val target = Paths.get(".", baseDir, "v1", fileName).toAbsolutePath
    val snakes = LinearDiff(source, target).shortestEditPath
    val html = DiffViewer.unifiedViewer(snakes)
    println(html)
  }
}
