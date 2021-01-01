resolvers += Resolver.bintrayRepo("oyvindberg", "converter")

addSbtPlugin("org.scoverage"     % "sbt-scoverage"       % "1.5.1")
addSbtPlugin("com.typesafe.sbt"  % "sbt-native-packager" % "1.3.2")
addSbtPlugin("com.github.gseitz" % "sbt-release"         % "1.0.7")
addSbtPlugin("com.eed3si9n"      % "sbt-assembly"        % "0.14.7")
addSbtPlugin("org.scala-js"      % "sbt-scalajs"         % "1.3.1")
addSbtPlugin("ch.epfl.scala"     % "sbt-scalajs-bundler" % "0.20.0")
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta29.1")
