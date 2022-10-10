package com.twilio.uscis.casestatus

import org.apache.http.NameValuePair
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{CloseableHttpResponse, HttpPost}
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.nio.charset.StandardCharsets
import java.util
import com.twitter.util.logging.{Logger, Logging}

object caseStatus {

  private val logger: Logger = Logger(this.getClass)
  private val url: String = "https://egov.uscis.gov/casestatus/mycasestatus.do"

  /**
   * Returns the text (content) from a REST URL as a String.
   * Returns a blank String if there was a problem.
   * This function will also throw exceptions if there are problems trying to connect to the url.
   *
   * @param caseNumber               USCIS case number
   * @param connectionTimeout        The connection timeout, in ms.
   * @param connectionRequestTimeout The connection request timeout, in ms.
   * @param socketTimeout            The socket timeout, in ms.
   */

  def getCaseStatus(caseNumber: String,
                    connectionTimeout: Int = 5000,
                    connectionRequestTimeout: Int = 5000,
                    socketTimeout: Int = 5000): String = {

    var caseStatus = ""

    val httpClient = buildHttpClient(connectionTimeout, connectionRequestTimeout, socketTimeout)
    val postRequest: HttpPost = new HttpPost(url)
    postRequest.addHeader("Content-Type", "application/x-www-form-urlencoded")
    val nameValuePairs = new util.ArrayList[NameValuePair](1)
    nameValuePairs.add(new BasicNameValuePair("changeLocale", ""))
    nameValuePairs.add(new BasicNameValuePair("completedActionsCurrentPage", "0"))
    nameValuePairs.add(new BasicNameValuePair("upcomingActionsCurrentPage", "0"))
    nameValuePairs.add(new BasicNameValuePair("appReceiptNum", caseNumber))
    nameValuePairs.add(new BasicNameValuePair("caseStatusSearchBtn", "CHECK+STATUS"))
    postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs))

    val httpResponse: CloseableHttpResponse = httpClient.execute(postRequest)
    val statusCode = httpResponse.getStatusLine.getStatusCode
    logger.debug("HTTP status code from HTTP POST request to " + url + " is " + statusCode)
    if (statusCode == 200) {
      val entity = httpResponse.getEntity
      if (entity != null) {
        val html = EntityUtils.toString(entity, StandardCharsets.UTF_8)
        val doc: Document = Jsoup.parse(html)

        // case status from div.current-status-sec
        val caseStatusSec = doc.getElementsByClass("current-status-sec")
        val caseStatusClean = caseStatusSec.select("span.appointment-sec-show")
        caseStatusClean.remove() // remove '+' from span.appointment-sec-show

        // error message from div.errorMessages
        val errorMessages = doc.getElementsByClass("errorMessages")

        if (errorMessages.text() == null || errorMessages.text().isEmpty) {
          caseStatus = caseStatusSec.text()
        }
        else {
          caseStatus = errorMessages.text()
        }
      }
    }
    httpClient.close()
    logger.debug("case status is " + caseStatus)
    caseStatus
  }

  private def buildHttpClient(connectionTimeout: Int, connectionRequestTimeout: Int, socketTimeout: Int): CloseableHttpClient = {

    var httpClient = HttpClients.createDefault()
    val requestConfig: RequestConfig = RequestConfig.custom()
      .setConnectTimeout(connectionTimeout)
      .setConnectionRequestTimeout(connectionRequestTimeout)
      .setSocketTimeout(socketTimeout).build
    httpClient = HttpClients.custom.setDefaultRequestConfig(requestConfig).build()

    httpClient

  }

}
