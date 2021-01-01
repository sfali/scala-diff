package com.alphasystem.diff.ui.pages

import com.alphasystem.diff.ui.router.AppRouter.Page
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.all._
import scalacss.ScalaCssReactImplicits

object HomePage extends ScalaCssReactImplicits {

  case class Props(router: RouterCtl[Page])

  class Backend(t: BackendScope[Props, Unit]) {
    def render(props: Props): VdomElement = {
      println(s"++++++++++++++++++")
      div("Welcome")
    }
  }

  private val component = ScalaComponent
    .builder[Props]("HomePage")
    .renderBackend[Backend]
    .build

  def apply(router: RouterCtl[Page]): Unmounted[Props, Unit, Backend] = component(Props(router))
}
