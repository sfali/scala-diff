package com.alphasystem.diff.cli

import com.alphasystem.diff.{Line, OperationType, Snake}
import scalatags.Text
import scalatags.Text.all._

object DiffViewer {

  def unifiedViewer(snakes: List[Snake[String]]): String = {
    "<!DOCTYPE html>" + html(lang := "en")(
      head(),
      body(
        table(style :=
          """border: none; border-collapse: collapse; table-layout: fixed; width: 100%;
            |font-family: 'Courier New', 'monospace';""".stripMargin
            .replaceAll(System.lineSeparator(), ""))(
          tbody(
            snakes.map {
              snake =>
                snake.operationType match {
                  case OperationType.Insertion => displayInsertion(snake.line)
                  case OperationType.Deletion => displayDeletion(snake.line)
                  case OperationType.Matched => displayMatched(snake.line)
                  case OperationType.None => displayNone(snake.line)
                }
            }
          ) // end of tbody
        ) // end of table
      ) // end of body
    ) // end of html
  }

  private def displayDeletion(line: Line[String]): Text.TypedTag[String] =
    tr(style := "background-color: #FFEEF0; color: #B31D28;")(
      td(style := "width: 2%;")(span("-")),
      td(style := "width: 5%;")(span(pre(f"${line.lineNumber}%6d"))),
      td(code(pre(line.text)))
    )

  private def displayInsertion(line: Line[String]): Text.TypedTag[String] =
    tr(style := "background-color: #F0FFF4; color: #22863A;")(
      td(style := "width: 2%;")(span("+")),
      td(style := "width: 5%;")(span(pre(f"${line.lineNumber}%6d"))),
      td(code(pre(line.text)))
    )

  private def displayMatched(line: Line[String]): Text.TypedTag[String] =
    tr()(
      td(style := "width: 2%;")(span(" ")),
      td(style := "width: 5%;")(span(pre(f"${line.lineNumber}%6d"))),
      td(code(pre(line.text)))
    )

  private def displayNone(line: Line[String]): Text.TypedTag[String] = tr(td(colspan := "3"))
}
