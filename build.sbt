
name := "scalatest-spring"

version := "0.0.1"

organization := "org.scalatest"

scalaVersion := "2.10.4"

val spring_version = "3.2.6.RELEASE"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.1.3",
  "org.springframework" % "spring-tx" % spring_version,
  "org.springframework" % "spring-context" % spring_version,
  "org.springframework" % "spring-test" % spring_version,
  "org.springframework" % "spring-orm" % spring_version,
  "cglib" % "cglib" % "3.1",
  "org.hibernate" % "hibernate-core" % "3.6.10.Final" % "test",
  "com.h2database" % "h2" % "1.4.183" % "test"
  )

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

scalacOptions ++= Seq("-deprecation", "-unchecked")

