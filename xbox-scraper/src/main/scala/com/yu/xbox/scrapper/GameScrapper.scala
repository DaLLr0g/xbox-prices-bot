package com.yu.xbox.scrapper

import sttp.client4.Response
import sttp.client4.UriContext
import sttp.client4.basicRequest
import sttp.client4.httpurlconnection.HttpURLConnectionBackend
import sttp.model.HeaderNames
import sttp.model.MediaType
import sttp.model.StatusCode.Ok

import io.circe.parser.decode
import net.ruippeixotog.scalascraper.dsl.DSL.Extract.*
import net.ruippeixotog.scalascraper.dsl.DSL.*
import com.yu.xbox.model.Currency
import com.yu.xbox.model.Game
import com.yu.xbox.model.GameListItem

object GameScrapper extends Scrapper[GameListItem, Option[Game]] {
  private val PricesUrl = "https://xbdeals.net/products/ajax-compare-prices?id="

  override def scrapPage(gameCard: GameListItem): Option[Game] = {
    val doc = browser.get(s"https://xbdeals.net${gameCard.link}")

    val gameContainer = doc >?> element(".game-container")

    for {
      cont  <- gameContainer
      title <- gameContainer >> text(".game-title-info-name")

      id <- gameContainer >> attr("content")("meta[itemprop='sku']")
      prices = getPrices(id)
    } yield {
      Game(
        title,
        id,
        prices
      )
    }
  }

  private def getPrices(id: String): List[Currency] = {
    val currencyPattern = """[^\d\s,.]+""".r

    for {
      pricesString <- getPricesElement(id).toList
      pricesElements = browser.parseString(pricesString) >> elementList("a")

      element <- pricesElements

      country     = element >> attr("title")
      countryCode = element >> text(".compare-prices-store-name")
      price       = element >> text(".compare-prices-price")
    } yield {
      Currency(
        country,
        countryCode,
        parseCurrencyAmount(price),
        currencyPattern.findFirstIn(price).getOrElse("")
      )
    }
  }

  private def parseCurrencyAmount(input: String): Double = {
    val numericPattern = """[\d,]+\.?\d*""".r
    numericPattern.findFirstIn(input) match {
      case Some(numberString) =>
        numberString.replaceAll(",", "").toDouble
      case None => 0.0
    }
  }

  private def getPricesElement(id: String): Option[String] = {
    val request = basicRequest
      .contentType(MediaType.ApplicationJson)
      .header(HeaderNames.UserAgent, UserAgent)
      .header(HeaderNames.XRequestedWith, XMLHttpRequest)
      .get(uri"$PricesUrl$id")

    val backend  = HttpURLConnectionBackend()
    val response = request.send(backend)

    response match
      case Response(Right(responseValue), Ok, _, _, _, _) =>
        decode[String](responseValue) match
          case Right(value) => Some(value)
          case Left(error)  => throw error
      case Response(body, code, statusText, headers, history, request) =>
        throw Exception(body.toString)

  }
}
