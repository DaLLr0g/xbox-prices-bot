package com.yu.xbox.currency

import sttp.client4.Response
import sttp.client4.UriContext
import sttp.client4.basicRequest
import sttp.client4.httpurlconnection.HttpURLConnectionBackend
import sttp.model.StatusCode.Ok

import io.circe.Decoder
import io.circe.parser.decode

case class Rates(rates: Map[String, Double])

object CurrencyApi {
  def rapidRequest(): Map[String, Double] = {

    val request = basicRequest
      .get(uri"https://exchange-rate-api1.p.rapidapi.com/latest?base=UAH")
      .header("X-RapidAPI-Key", "825bf521bdmsh5190ce85be570eep16910bjsnd69f129334aa")
      .header("X-RapidAPI-Host", "exchange-rate-api1.p.rapidapi.com")

    val backend  = HttpURLConnectionBackend()
    val response = request.send(backend)

    response match
      case Response(Right(responseValue), Ok, _, _, _, _) =>
        decode[Map[String, Double]](responseValue)(Decoder.instance(_.downField("rates").as[Map[String, Double]])) match
          case Right(rates) => rates
          case Left(error)  => throw error
      case Response(body, code, statusText, headers, history, request) =>
        throw Exception(body.toString)
  }
}
