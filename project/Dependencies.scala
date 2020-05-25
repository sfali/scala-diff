import sbt._
import Keys._

object Dependencies {

  private val AkkaVersion = "2.6.5"
  private val AlpakkaVersion = "2.0.0"
  private val CirceVersion = "0.13.0"
  private val EnumeratumVersion = "1.5.15"
  private val EnumeratumCirceVersion = "1.5.23"
  private val ScalatestVersion = "3.3.0-SNAP2"
  private val ScalacheckVersion = "1.14.3"
  private val LogbackVersion = "1.2.3"
  private val ScoptVersion = "4.0.0-RC2"
  private val ComTypesafeAkka = "com.typesafe.akka"
  private val ComLightbendAkka = "com.lightbend.akka"
  private val IoCirce = "io.circe"
  private val ComBeachape = "com.beachape"
  private val OrgScalatest = "org.scalatest"
  private val OrgScalacheck = "org.scalacheck"
  private val ChQosLogback = "ch.qos.logback"
  private val ComGithubScopt = "com.github.scopt"

  val Common = Seq(
    // These libraries are added to all modules via the `Common` AutoPlugin
    libraryDependencies ++= Seq(
      IoCirce             %% "circe-core"                 % CirceVersion,
      IoCirce             %% "circe-generic"              % CirceVersion,
      IoCirce             %% "circe-parser"               % CirceVersion,
      ComBeachape         %% "enumeratum"                 % EnumeratumVersion,
      ComBeachape         %% "enumeratum-circe"           % EnumeratumCirceVersion,
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
      ComGithubScopt      %% "scopt"                      % ScoptVersion
    )
  )
}
