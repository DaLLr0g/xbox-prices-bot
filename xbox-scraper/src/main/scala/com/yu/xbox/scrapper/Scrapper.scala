package com.yu.xbox.scrapper

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.browser.HtmlUnitBrowser
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.jsoup.Connection

trait Scrapper[FindBy, Return] {

  val browser: JsoupBrowser = new JsoupBrowser(UserAgent)

  def scrapPage(url: FindBy): Return

  protected def getPage(url: String)(headers: Map[String, String]): browser.DocumentType = {
    browser.get(url)
  }
}
