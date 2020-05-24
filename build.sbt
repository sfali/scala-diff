import Dependencies._

lazy val diff = createProject("diff", "diff")

lazy val modules: Seq[ProjectReference] = Seq(diff)

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
}