package com.alphasystem.diff.ui.components

import com.alphasystem.diff.Snake
import com.alphasystem.diff.ui.css.DiffStyles
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.all._
import scalacss.ScalaCssReactImplicits

object UnifiedDiffViewer extends ScalaCssReactImplicits {

  case class Props(snakes: List[Snake[String]])

  class Backend(backend: BackendScope[Props, Unit]) {
    def render(props: Props): VdomElement =
      div()(
        DiffStyles.table
      )
  }

  private val component = ScalaComponent
    .builder[Props]("UnifiedDiffViewer")
    .renderBackend[Backend]
    .build

  def apply(snakes: List[Snake[String]]): Unmounted[Props, Unit, Backend] = component(Props(snakes))
}
