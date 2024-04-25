package com.yu.xbox.currency

import sttp.client4.Response
import sttp.client4.UriContext
import sttp.client4.basicRequest
import sttp.client4.httpurlconnection.HttpURLConnectionBackend
import sttp.model.StatusCode.Ok

import io.circe.Decoder
import io.circe.parser.decode

object CurrencyApi {
  def rapidRequest(): Map[String, Double] = {

    val request = basicRequest
      .get(uri"https://exchange-rate-api1.p.rapidapi.com/latest?base=UAH")
      .header("X-RapidAPI-Key", "825bf521bdmsh5190ce85be570eep16910bjsnd69f129334aa")
      .header("X-RapidAPI-Host", "exchange-rate-api1.p.rapidapi.com")

    val backend  = HttpURLConnectionBackend()
    val response = request.send(backend)

    response.body
      .flatMap(responseBody =>
        decode[Map[String, Double]](responseBody)(Decoder.instance(_.downField("rates").as[Map[String, Double]]))
      ) match
      case Left(error: io.circe.Error) => throw error
      case Left(error: String)         => throw new Exception(error)
      case Right(value)                => value
  }
}
