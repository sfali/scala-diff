import sbt._
import Keys._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {

  private val ReactVersion = "16.5.2"
  private val TypeReactVersion = "17.0.0"
  private val AkkaVersion = "2.6.5"
  private val AlpakkaVersion = "2.0.0"
  private val CirceVersion = "0.13.0"
  private val EnumeratumVersion = "1.6.1"
  private val EnumeratumCirceVersion = "1.6.1"
  private val ScalatestVersion = "3.3.0-SNAP2"
  private val ScalacheckVersion = "1.14.3"
  private val LogbackVersion = "1.2.3"
  private val ScoptVersion = "4.0.0-RC2"
  private val ScalaTagsVersion = "0.9.2"
  private val ScalacssVersion = "0.6.1"
  private val ScalaJsVersion = "1.1.0"
  private val ScalaJsReactVersion = "1.7.7"
  private val CatsCoreVersion = "2.1.1"
  private val ComTypesafeAkka = "com.typesafe.akka"
  private val ComLightbendAkka = "com.lightbend.akka"
  private val IoCirce = "io.circe"
  private val ComBeachape = "com.beachape"
  private val OrgScalatest = "org.scalatest"
  private val OrgScalacheck = "org.scalacheck"
  private val ChQosLogback = "ch.qos.logback"
  private val ComGithubScopt = "com.github.scopt"
  private val OrgScalaJs = "org.scala-js"
  private val ComLihaoyi = "com.lihaoyi"
  private val ScalaJsReact = "com.github.japgolly.scalajs-react"
  private val Scalacss  = "com.github.japgolly.scalacss"
  private val OrgTypelevel = "org.typelevel"

  val Common = Seq(
    // These libraries are added to all modules via the `Common` AutoPlugin
    libraryDependencies ++= Seq(
      IoCirce             %%% "circe-core"                 % CirceVersion,
      IoCirce             %%% "circe-generic"              % CirceVersion,
      IoCirce             %%% "circe-parser"               % CirceVersion,
      ComBeachape         %%% "enumeratum"                 % EnumeratumVersion,
      ComBeachape         %%% "enumeratum-circe"           % EnumeratumCirceVersion,
      ChQosLogback        %  "logback-classic"            % LogbackVersion,
      OrgScalatest        %% "scalatest"                  % ScalatestVersion         % Test,
      OrgScalacheck       %% "scalacheck"                 % ScalacheckVersion        % Test
    )
  )

  val Client = Seq(
    libraryDependencies ++= Seq(
      ComTypesafeAkka     %% "akka-actor-typed"           % AkkaVersion,
      ComTypesafeAkka     %% "akka-stream-typed"          % AkkaVersion,
      ComLightbendAkka    %% "akka-stream-alpakka-csv"    % AlpakkaVersion
        excludeAll ExclusionRule(organization = ComTypesafeAkka),
      ComLightbendAkka    %% "akka-stream-alpakka-file"   % AlpakkaVersion
        excludeAll ExclusionRule(organization = ComTypesafeAkka)
    )
  )

  val Cli = Seq(
    libraryDependencies ++= Seq(
      ComGithubScopt      %% "scopt"                      % ScoptVersion,
      ComLihaoyi          %% "scalatags"                  % ScalaTagsVersion,
      Scalacss            %% "ext-scalatags"              % ScalacssVersion
    )
  )

  val UI = Seq(
    libraryDependencies ++= Seq(
      OrgTypelevel        %%% "cats-core"                  % CatsCoreVersion,
      OrgScalaJs          %%% "scalajs-dom"                % ScalaJsVersion,
      ScalaJsReact        %%% "core"                       % ScalaJsReactVersion,
      ScalaJsReact        %%% "extra"                      % ScalaJsReactVersion,
      Scalacss            %%% "core"                       % ScalacssVersion,
      Scalacss            %%% "ext-react"                  % ScalacssVersion
    )
  )

  val NpmDependencies =  Def.setting(
    Seq(
      "react" -> ReactVersion,
      "react-dom" -> ReactVersion,
      "@types/react" -> TypeReactVersion,
      "@types/react-dom" -> TypeReactVersion
    )
  )

}
