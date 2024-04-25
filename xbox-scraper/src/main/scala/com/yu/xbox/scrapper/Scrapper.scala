package com.yu.xbox.scrapper

import cats.effect.IO
import cats.effect.IOApp
import cats.effect.kernel.Async
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.browser.HtmlUnitBrowser
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.jsoup.Connection

import com.yu.xbox.scrapper.Scrapper.UserAgent

object Scrapper {
  val UserAgent =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36"
  val XMLHttpRequest = "XMLHttpRequest"
}

trait Scrapper[F[_]: Async, FindBy, Return] {

  val browser: JsoupBrowser = new JsoupBrowser(UserAgent)

  def scrapPage(url: FindBy): F[Return]
}

object Main extends IOApp.Simple {

  override def run: IO[Unit] = for {
    gameList <- new GameListScrapper[IO]().scrapPage("Cult")
    _        <- IO.println(gameList.mkString("\n"))
  } yield ()
}
