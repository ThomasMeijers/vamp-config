import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import sbt.Keys._

import scala.language.postfixOps
import scalariform.formatter.preferences._

organization := "io.vamp"

name := "vamp-config"

version := VersionHelper.versionByTag

scalaVersion := "2.12.1"

scalariformSettings ++ Seq(ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignParameters, true)
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(DanglingCloseParenthesis, Preserve)
  .setPreference(RewriteArrowSymbols, true))

resolvers += Resolver.sonatypeRepo("releases")


lazy val root = project.in(sbt.file(".")).settings(packAutoSettings ++ Seq(packExcludeJars := Seq("scala-.*\\.jar"))).settings(
  libraryDependencies ++= Seq(
    "com.typesafe" % "config" % "1.3.1",
     "com.chuusai" %% "shapeless" % "2.3.2"
  )
)

scalacOptions += "-target:jvm-1.8"

javacOptions ++= Seq("-encoding", "UTF-8")

scalacOptions in ThisBuild ++= Seq(Opts.compile.deprecation, Opts.compile.unchecked) ++ Seq("-Ywarn-unused-import", "-Ywarn-unused", "-Xlint", "-feature")