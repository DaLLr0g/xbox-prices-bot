package com.yu.xbox.model

case class GameListItem(
  title: String,
  price: String,
  picture: String,
  link: String
) {
  private val idRegex = ".+/game/(\\d+)/.+".r
  val id: Option[Int] =
    idRegex
      .findFirstIn(link)
      .map { case idRegex(v) => v.toInt }
}

case class Game(
  title: String,
  picture: String,
  prices: Seq[Currency]
)

case class Currency(
  country: String,
  countryCode: String,
  cost: Double,
  symbol: String
)
