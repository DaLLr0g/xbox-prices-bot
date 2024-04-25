package com.yu.xbox.scrapper

import cats.effect.IO
import cats.effect.kernel.Async
import cats.instances.list.*
import cats.syntax.applicative._
import cats.syntax.traverse.*
import net.ruippeixotog.scalascraper.dsl.DSL.Extract.*
import net.ruippeixotog.scalascraper.dsl.DSL.*
import net.ruippeixotog.scalascraper.model.*

import com.yu.xbox.model.GameListItem

class GameListScrapper[F[_]: Async] extends Scrapper[F, String, List[GameListItem]] {

  private val Link = "https://xbdeals.net/us-store/search?search_query="

  private val TitleQuery     = text(".game-collection-item-details-title")
  private val LinkQuery      = attr("href")(".game-collection-item-link")
  private val PriceQuery     = text(".game-collection-item-price ")
  private val PricelessQuery = text(".game-collection-item-price")
  private val PictureQuery =
    element(".game-collection-item-image-placeholder") >> attr("data-src")(".game-collection-item-image")

  override def scrapPage(gameKey: String): F[List[GameListItem]] = {
    val doc       = browser.get(s"$Link$gameKey")
    val cardItems = doc >> elementList(".game-collection-item")
    val res = for {
      item <- cardItems

      title   = item >> TitleQuery
      link    = item >> LinkQuery
      picture = item >> PictureQuery
      price   = item >?> PriceQuery
    } yield GameListItem(
      title,
      price.getOrElse(""),
      picture,
      link
    ).pure

    res.traverse(identity)
  }

}
