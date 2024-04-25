package com.yu.xbox.bot

import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Sync
import pureconfig.*
import pureconfig.generic.derivation.default.*
import pureconfig.module.catseffect.syntax.*

case class Config(botApiToken: String) derives ConfigReader

object Config {
  def load[F[_]: Sync]: F[Config] =
    ConfigSource.default.loadF[F, Config]()
}
