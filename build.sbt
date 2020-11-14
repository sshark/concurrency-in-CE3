
addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.11.0" cross CrossVersion.full)
addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")

lazy val root = (project in file("."))
  .enablePlugins(GitVersioning, BuildInfoPlugin)
  .settings(
    name := "",
    version := "1.0.0-SNAPSHOT",
    organization := "org.teckhooi",
    scalaVersion := "2.13.3",
    fork in Test := true,
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, git.baseVersion, git.gitHeadCommit),
    buildInfoPackage := "org.teckhooi.concurrentCE3",
    buildInfoUsePackageAsPath := true,
    libraryDependencies ++= Seq(
      "io.chrisdavenport" %% "log4cats-slf4j" % "1.1.1",
      "ch.qos.logback"    % "logback-classic" % "1.3.0-alpha5"
    )
  )
