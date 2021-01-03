package com.alphasystem.diff.ui.pages

import com.alphasystem.diff.OperationType
import com.alphasystem.diff.ui.css.DiffStyles
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.all._
import scalacss.ScalaCssReactImplicits

object HomePage extends ScalaCssReactImplicits {

  case class Props()

  class Backend(backend: BackendScope[Props, Unit]) {
    def render(props: Props): VdomElement =
      div()(
        div(`class` := "row gx-2")(
          div(`class` := "col")(
            DiffStyles.insertion,
            div(id := "source", `class` := "p-1 border bg-light ")(
              i(`class` := "bi-alarm")(OperationType.Insertion.entryName)
            )
          ),
          div(`class` := "col")(
            div(id := "target", `class` := "p-1 border bg-light")(
              DiffStyles.deletion,
              i(`class` := "bi-alarm")(OperationType.Deletion.entryName)
            )
          )
        )
      )
  }

  private val component = ScalaComponent
    .builder[Props]("HomePage")
    .renderBackend[Backend]
    .build

  def apply(): Unmounted[Props, Unit, Backend] = component(Props())
}
