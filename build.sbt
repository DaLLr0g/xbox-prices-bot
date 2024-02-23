ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

val scalaFixSettings = Seq(
  semanticdbEnabled := true,
  scalafixDependencies += "com.github.xuwei-k" %% "scalafix-rules" % "0.3.5"
)

inThisBuild(
    scalaFixSettings
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
    name := "xbox-prices-bot"
  )

addCommandAlias("fmtAll", "scalafmtAll; scalafmtSbt; scalafixAll")
