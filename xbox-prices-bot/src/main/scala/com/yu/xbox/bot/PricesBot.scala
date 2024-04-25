package com.yu.xbox.bot

import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

import telegramium.bots.CallbackQuery
import telegramium.bots.ChatId
import telegramium.bots.ChatIntId
import telegramium.bots.InlineKeyboardButton
import telegramium.bots.InlineKeyboardMarkup
import telegramium.bots.InputLinkFile
import telegramium.bots.InputMediaPhoto
import telegramium.bots.MaybeInaccessibleMessage
import telegramium.bots.Message
import telegramium.bots.ReplyKeyboardMarkup
import telegramium.bots.high.Api
import telegramium.bots.high.LongPollBot
import telegramium.bots.high.implicits.*
import telegramium.bots.high.keyboards.InlineKeyboardButtons
import telegramium.bots.high.keyboards.InlineKeyboardMarkups

import cats.Parallel
import cats.effect.Async
import cats.syntax.all.*
import org.http4s.client.Client

import com.yu.xbox.model.GameListItem
import com.yu.xbox.scrapper.GameListScrapper
import com.yu.xbox.scrapper.GameScrapper

class PricesBot[F[_]: Async: Parallel](
  httpClient: Client[F]
)(using
  botApi: Api[F]
) extends LongPollBot[F](botApi) {

  extension (msg: Message) {
    def chatId: ChatId = ChatIntId(msg.chat.id)
  }

  override def onMessage(msg: Message): F[Unit] = {
    val reply = for {
      text <- msg.text
    } yield sendGames(text, msg.chatId).void

    reply.getOrElse(Async[F].unit)
  }

  override def onCallbackQuery(query: CallbackQuery): F[Unit] = {
    def onMsg(message: Option[MaybeInaccessibleMessage])(f: Message => F[Unit]): F[Unit] =
      message.collect { case m: Message => m }.fold(Async[F].unit)(f)

    def setSelectedGame(gameId: String)(msg: Message): F[Unit] = {
      val gameNameOpt = msg.replyMarkup.flatMap(_.inlineKeyboard.flatten.find(_.callbackData.exists(_ == gameId)))
        .map(_.text)
        .map(_.filter(c => c.isLetterOrDigit || c.isSpaceChar))
        .filterNot(_.isBlank)
        .map(_.toLowerCase)

      for {
        game <- Async[F].fromOption(gameNameOpt, new RuntimeException())
        selectedGame <- new GameScrapper[F]().scrapPage(GameListItem(null, null, null,
          s"/us-store/game/$gameId/${game.replaceAll(" ", "-")}"
        ))
        _ <- editMessageMedia(
          chatId = Some(ChatIntId(msg.chat.id)),
          media = InputMediaPhoto(selectedGame.picture, caption = selectedGame.title.some),
          messageId = Some(msg.messageId),
          replyMarkup = msg.replyMarkup
        ).exec.void
        _ <- answerCallbackQuery(callbackQueryId = query.id).exec
      } yield ()
//        editMessageText(
//          chatId = Some(ChatIntId(msg.chat.id)),
//          messageId = Some(msg.messageId),
//          text = s"You have chosen: $flavor"
//        ).exec.void
    }

    query.data.map( gameId =>
      onMsg(query.message)(setSelectedGame(gameId))
    ).getOrElse(Async[F].unit)
  }

  private def sendGames(keyword: String, id: ChatId) = {
    def sendGameList(games: List[GameListItem]) =
      sendPhoto(
        chatId = id,
        photo = InputLinkFile("https://duet-cdn.vox-cdn.com/thumbor/0x0:3000x2000/2400x1600/filters:focal(1500x1000:1501x1001):format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/24763499/xboxlogo.jpg"),
        caption = Some("Found games:"),
        replyMarkup = Some(
          InlineKeyboardMarkups.singleColumn(
            games.map(g => InlineKeyboardButton(
              text = s"${g.title}",
              callbackData = g.id.map(_.toString)
            ))
          )
        )
      ).exec

    for {
      games <- findGames(keyword)
      msg <- sendGameList(games)
    } yield msg
  }

  private def findGames(keyword: String): F[List[GameListItem]] =
    new GameListScrapper().scrapPage(keyword)
//    List(
//        GameListItem("Cult of the Lamb: Cultist Edition","760.00₴","https://store-images.s-microsoft.com/image/apps.3196.14555503020307101.0afc2dfa-1bbc-4dcc-95bd-f5352d11f513.5529a992-6553-42d0-97cb-886dd7b94f8e?format=jpg&w=192&h=192","/ua-store/game/958158/cult-of-the-lamb-cultist-edition"),
//        GameListItem("Cult of the Lamb: Heretic Edition","895.00₴","https://store-images.s-microsoft.com/image/apps.56882.14522476222798993.39c38880-66a5-431d-a004-1963bd25e491.5f9f0308-71a3-47de-975f-d3006df7460f?format=jpg&w=192&h=192","/ua-store/game/1078060/cult-of-the-lamb-heretic-edition"),
//        GameListItem("Cult of the Lamb","640.00₴","https://store-images.s-microsoft.com/image/apps.9769.14346269945886296.3fed052e-4ece-489a-b79f-61e67b7dcead.64602780-f60a-4afb-9e0a-40e4bf664492?format=jpg&w=192&h=192","/ua-store/game/958156/cult-of-the-lamb")
//        ).pure
}
