import Dependencies._

lazy val diff = createProject("diff", "diff")

lazy val client = createProject("client", "client", Client)
  .aggregate(diff)
  .dependsOn(diff)

lazy val cli = createProject("cli", "cli", Cli)
  .aggregate(client)
  .dependsOn(client)

lazy val modules: Seq[ProjectReference] = Seq(diff, client, cli)

lazy val `scala-diff` = project
  .in(file("."))
  .aggregate(modules: _*)

def createProject(projectId: String, _moduleName: String, additionalSettings: sbt.Def.SettingsDefinition*): Project = {
  Project(id = projectId, base = file(projectId))
    .settings(
      name := projectId,
      moduleName := _moduleName
    )
    .settings(additionalSettings: _*)
    .enablePlugins(JavaAppPackaging)
}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
