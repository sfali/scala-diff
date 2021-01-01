package com.alphasystem.diff.ui.css

import CssSettings._

import scala.language.postfixOps

object DiffStyles extends StyleSheet.Inline {

  import dsl._

  private val codeFont = fontFace("codeFont")(
    _.src("local(Courier New)", "local(monospace)")
  )

  val table: StyleA = style(
    width(100 %%),
    display.table,
    fontFamily(codeFont)
  )

  val tableRow: StyleA = style(
    display.tableRow
  )

  val tableCell: StyleA = style(
    display.tableCell
  )

  val deletion: StyleA = style(
    tableRow,
    backgroundColor(c"#FFEEF0"),
    color(c"#B31D28")
  )

  val insertion: StyleA = style(
    tableRow,
    backgroundColor(c"#F0FFF4"),
    color(c"#22863A")
  )
}
