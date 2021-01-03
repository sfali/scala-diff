package com.alphasystem.diff.cli.viewer

import com.alphasystem.diff.cli.viewer.CssSettings._
import scalacss.internal.ValueT

object ViewerStyles extends StyleSheet.Inline {

  import dsl._

  val table: StyleA = style(
    width(ValueT("100%")),
    display.table
    //fontFamily()
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
