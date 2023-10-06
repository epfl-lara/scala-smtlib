enablePlugins(GitVersioning)

git.useGitDescribe := true

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

IntegrationTest / javaOptions ++= Seq("-Xss128M")

IntegrationTest / fork := true

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % "test;it"


IntegrationTest / logBuffered := false

Test / parallelExecution := true

lazy val commonSettings = Seq(
  organization := "com.regblanc",
  name := "scala-smtlib",
  scalaVersion := "3.3.0",
)

lazy val root = (project in file(".")).
  configs(IntegrationTest).
  settings(commonSettings: _*).
  settings(Defaults.itSettings: _*)

publishMavenStyle := true

Test / publishArtifact := false

pomIncludeRepository := { _ => false }

licenses := Seq("MIT-style" -> url("https://opensource.org/licenses/MIT"))

pomExtra := (
  <url>https://github.com/epfl-lara/scala-smtlib</url>
    <scm>
      <url>git@github.com:epfl-lara/scala-smtlib.git</url>
      <connection>scm:git:git@github.com:epfl-lara/scala-smtlib.git</connection>
    </scm>
    <developers>
      <developer>
        <id>reg</id>
        <name>Regis Blanc</name>
        <url>http://regblanc.com</url>
      </developer>
    </developers>
)
