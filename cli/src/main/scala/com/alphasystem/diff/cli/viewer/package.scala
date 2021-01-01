package com.alphasystem.diff.cli

import scalacss.defaults.Exports
import scalacss.internal.mutable.Settings

package object viewer {
  val CssSettings: Exports with Settings = scalacss.devOrProdDefaults
}
