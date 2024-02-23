import sbt.*

object Dependencies {

  object V {
    val scalatest     = "3.2.17"
    val scalaScrapper = "3.1.1"
    val sttp          = "4.0.0-M8"
    val circe         = "0.14.6"
  }

  private val sttp          = "com.softwaremill.sttp.client4" %% "core"          % V.sttp
  private val scalaScrapper = "net.ruippeixotog"              %% "scala-scraper" % V.scalaScrapper

  private val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % V.circe)

  private val scalatest = "org.scalatest" %% "scalatest" % V.scalatest % Test

  val common: Seq[ModuleID] =
    circe :+ scalatest

  val xboxScraper: Seq[ModuleID] =
    common ++ Seq(
      sttp,
      scalaScrapper
    )
}
