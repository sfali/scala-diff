package com.alphasystem.diff.ui.css

import CssSettings._
import scalacss.internal.mutable.GlobalRegistry

object AppCSS {

  def load(): Unit = {
    GlobalRegistry.register(
      DiffStyles
    )
    GlobalRegistry.onRegistration(_.addToDocument())
  }

}
