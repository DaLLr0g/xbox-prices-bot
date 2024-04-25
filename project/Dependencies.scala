import sbt.*

object Dependencies {

  object V {
    // common
    val circe      = "0.14.6"
    val log4cats   = "2.6.0"
    val cats       = "2.10.0"
    val catsEffect = "3.5.4"
    val logback    = "1.5.6"
    val pureConfig = "0.17.6"

    // scrapper
    val scalaScrapper = "3.1.1"
    val sttp          = "4.0.0-M8"

    // bot
    val telegramium = "8.72.0"

    // tests
    val scalatest = "3.2.18"
  }

  private val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % V.circe)

  private val cats = Seq(
    "org.typelevel" %% "cats-core"      % V.cats,
    "org.typelevel" %% "log4cats-slf4j" % V.log4cats,
    "org.typelevel" %% "cats-effect"    % V.catsEffect
  )

  private val scalaScrapper = "net.ruippeixotog"              %% "scala-scraper" % V.scalaScrapper
  private val sttp          = "com.softwaremill.sttp.client4" %% "core"          % V.sttp
  private val pureConfig = Seq(
    "com.github.pureconfig" %% "pureconfig-core",
    "com.github.pureconfig" %% "pureconfig-cats-effect"
  ).map(_ % V.pureConfig)

  private val telegramium = Seq(
    "io.github.apimorphism" %% "telegramium-core",
    "io.github.apimorphism" %% "telegramium-high"
  ).map(_ % V.telegramium)

  private val scalatest = "org.scalatest" %% "scalatest"       % V.scalatest % Test
  private val logback   = "ch.qos.logback" % "logback-classic" % V.logback
  val common: Seq[ModuleID] =
    circe ++
      pureConfig ++
      cats :+
      scalatest :+
      logback

  val xboxBot: Seq[ModuleID] =
    telegramium
//      :+ ("org.slf4j" % "slf4j-api" % "2.0.13") :+ ("org.slf4j" % "slf4j-simple" % "2.0.13" % Test)

  val xboxScraper: Seq[ModuleID] = Seq(sttp, scalaScrapper)
}
