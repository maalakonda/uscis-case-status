name := "uscis-case-status"

organization := "com.twilio"

scalaVersion := "2.13.9"

version := "0.1.1-SNAPSHOT"

logLevel := Level.Info

libraryDependencies ++= {
  Seq(
    "com.twitter"   %% "finatra-http"    % "21.2.0",
    "com.twilio.sdk" % "twilio"          % "9.0.0",
    "ch.qos.logback" % "logback-classic" % "1.2.11", // with java 8 - it should be 1.2.11
    "org.jsoup"      % "jsoup"           % "1.15.3",
    "io.netty"       % "netty-transport-native-epoll" % "4.1.51.Final"
  )
}
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.4"

// to fix assembly
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.2.2" % "provided"  // to fix assembly
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
}

// fix - Exception: sbt.TrapExitSecurityException thrown from the UncaughtExceptionHandler in thread "run-main-0"
trapExit := false

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

scalacOptions += "-deprecation"

// dockerBaseImage := "azul/zulu-openjdk:8-latest"
// dockerBaseImage := "azul/zulu-openjdk:11-latest"
