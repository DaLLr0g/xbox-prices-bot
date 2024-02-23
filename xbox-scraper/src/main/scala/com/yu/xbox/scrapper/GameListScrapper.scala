package com.yu.xbox.scrapper

import net.ruippeixotog.scalascraper.dsl.DSL.Extract.*
import net.ruippeixotog.scalascraper.dsl.DSL.*
import net.ruippeixotog.scalascraper.model.*

import com.yu.xbox.model.GameListItem

object GameListScrapper extends Scrapper[String, List[GameListItem]] {

  private val Link = "https://xbdeals.net/ua-store/search?search_query="

  private val TitleQuery = text(".game-collection-item-details-title")
  private val LinkQuery  = attr("href")(".game-collection-item-link")
  private val PriceQuery = text(".game-collection-item-price")
  private val PictureQuery =
    element(".game-collection-item-image-placeholder") >> attr("data-src")(".game-collection-item-image")

  override def scrapPage(gameKey: String): List[GameListItem] = {
    val doc = browser.get(s"$Link$gameKey")

    val cardItems = doc >> elementList(".game-collection-item")
    for {
      item <- cardItems

      title   = item >> TitleQuery
      link    = item >> LinkQuery
      picture = item >> PictureQuery
      price   = item >> PriceQuery
    } yield GameListItem(
      title,
      price,
      picture,
      link
    )
  }
}
