package com.alphasystem.diff.ui

import org.scalajs.dom._
import com.alphasystem.diff.ui.css.AppCSS
import com.alphasystem.diff.ui.router.AppRouter

object App {

  def main(args: Array[String]): Unit = {
    AppRouter.router().renderIntoDOM(document.getElementById("root"))
    AppCSS.load()
  }

}
