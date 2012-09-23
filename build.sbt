name := "Zombie Escape Plan"

version := "1.0"

scalaVersion := "2.9.2"

libraryDependencies ++= Seq("org.specs2" %% "specs2" % "1.12.1" % "test")

libraryDependencies += "org.joda" % "joda-convert" % "1.2"

libraryDependencies += "joda-time" % "joda-time" % "2.0"

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                  "releases"  at "http://oss.sonatype.org/content/repositories/releases")
