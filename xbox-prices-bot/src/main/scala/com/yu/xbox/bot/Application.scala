package com.yu.xbox.bot

import telegramium.bots.high.Api
import telegramium.bots.high.BotApi

import cats.Parallel
import cats.effect.Async
import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.syntax.functor.*
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.client.Client
import org.http4s.client.middleware.Logger

object Application extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      config <- Config.load[IO]
      bot    <- runBot[IO](config.botApiToken)
    } yield bot

  /** @param token
    *   Bot API token got from Botfather
    */
  private def createBotBackend[F[_]: Async](http: Client[F], token: String) =
    BotApi(http, baseUrl = s"https://api.telegram.org/bot$token")

  private def runBot[F[_]: Async: Parallel](token: String) =
    BlazeClientBuilder[F].resource
      .use { httpClient =>
        val http            = Logger(logBody = false, logHeaders = false)(httpClient)
        given api: Api[F]   = createBotBackend(http, token)
        val urlShortenerBot = new PricesBot[F](httpClient)

        urlShortenerBot.start().as(ExitCode.Success)
      }

}
