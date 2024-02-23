package com.yu.xbox.model

case class GameListItem(
  title: String,
  price: String,
  picture: String,
  link: String
)

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
