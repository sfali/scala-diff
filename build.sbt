import Dependencies._

lazy val diffP = crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file("diff")

lazy val diffJvm = diffP.jvm.settings(name := "diff", moduleName := "diff")

lazy val diffJs = diffP.js.settings(name := "diffJs", moduleName := "diffJs")

lazy val client = createProject("client", "client", Seq(Client))
  .aggregate(diffJvm)
  .dependsOn(diffJvm)

lazy val cli = createProject("cli", "cli", Seq(Cli))
  .aggregate(client)
  .dependsOn(client)

lazy val ui = createProject("ui", "ui", Seq(UI),
  Seq(ScalablyTypedConverterPlugin))
  .aggregate(diffJs)
  .dependsOn(diffJs)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.CommonJSModule)
    },
    jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),
    scalaJSLinkerConfig ~= (_.withSourceMap(false)),
    webpackDevServerExtraArgs := Seq("--inline"),
    yarnExtraArgs := Seq("--silent"),
    npmDependencies in Compile ++= Dependencies.NpmDependencies.value,
    npmResolutions in Compile ++= (npmDependencies in Compile).value.toMap,
    webpackConfigFile in fastOptJS := Some(baseDirectory.value / "dev.webpack.config.js"),
    skip in publish := true
  )

lazy val modules = Seq(diffJvm, diffJs, client, cli, ui).map(Project.projectToRef)

lazy val `scala-diff` = project
  .in(file("."))
  .aggregate(modules: _*)

def createProject(projectId: String,
                  _moduleName: String,
                  additionalSettings: Seq[sbt.Def.SettingsDefinition] = Seq(),
                  plugins: Seq[sbt.Plugins] = Seq(JavaAppPackaging)): Project = {
  Project(id = projectId, base = file(projectId))
    .settings(
      name := projectId,
      moduleName := _moduleName
    )
    .settings(additionalSettings: _*)
    .enablePlugins(plugins: _*)
}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
