package com.alphasystem.diff.cli

import com.alphasystem.diff.{Line, OperationType, Snake}
import scalatags.Text
import scalatags.Text.all._

object DiffViewer {

  def unifiedViewer(snakes: List[Snake[String]]): String = {
    "<!DOCTYPE html>" + html(lang := "en")(
      head(),
      body(
        div(style := "width: 100%; display: table; font-family: 'Courier New', 'monospace'")(
          div(style := "display: table-row-group; line-height: 20%;")(
            snakes.map {
              snake =>
                snake.operationType match {
                  case OperationType.Insertion => displayInsertion(snake.line)
                  case OperationType.Deletion => displayDeletion(snake.line)
                  case OperationType.Matched => displayMatched(snake.line)
                  case OperationType.None => displayNone(snake.line)
                }
            }
          ) // end of div (table body)
        ) // end of div (table)
      ) // end of body
    ) // end of html
  }

  private def displayDeletion(line: Line[String]): Text.TypedTag[String] =
    div(style := "display: table-row; background-color: #FFEEF0; color: #B31D28;")(
      div(style := "display: table-cell; width: 3%")(pre(f"${line.lineNumber}%6d")),
      div(style := "display: table-cell; width: 2%")(span("- ")),
      div(style := "display: table-cell")(span(code(pre(line.text))))
    )

  private def displayInsertion(line: Line[String]): Text.TypedTag[String] =
    div(style := "display: table-row; background-color: #F0FFF4; color: #22863A;")(
      div(style := "display: table-cell; width: 3%")(pre(f"${line.lineNumber}%6d")),
      div(style := "display: table-cell; width: 2%")(span("+ ")),
      div(style := "display: table-cell")(span(code(pre(line.text))))
    )

  private def displayMatched(line: Line[String]): Text.TypedTag[String] =
    div(style := "display: table-row;")(
      div(style := "display: table-cell; width: 3%")(pre(f"${line.lineNumber}%6d")),
      div(style := "display: table-cell; width: 2%")(span("  ")),
      div(style := "display: table-cell")(span(code(pre(line.text))))
    )

  private def displayNone(line: Line[String]): Text.TypedTag[String] = div(style := "display: table-row")
}
