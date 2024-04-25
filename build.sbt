ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.1"

val settings = Seq(
  semanticdbEnabled                            := true,
  scalafixDependencies += "com.github.xuwei-k" %% "scalafix-rules" % "0.3.5",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-unchecked",
    "-language:postfixOps"
  ),
  libraryDependencies ++= Dependencies.common
)

inThisBuild(
  settings
)

lazy val root = (project in file("."))
  .aggregate(xboxScraper, xboxPricesBot)
  .settings(
    name := "xbox-prices"
  )

lazy val xboxScraper = (project in file("xbox-scraper"))
  .settings(
    name := "xbox-scraper",
    libraryDependencies ++= Dependencies.xboxScraper
  )

lazy val xboxPricesBot = (project in file("xbox-prices-bot"))
  .settings(
    name := "xbox-prices-bot",
    libraryDependencies ++= Dependencies.xboxBot
  )
  .dependsOn(xboxScraper)

addCommandAlias("fmtAll", "scalafmtAll; scalafmtSbt; scalafixAll")
