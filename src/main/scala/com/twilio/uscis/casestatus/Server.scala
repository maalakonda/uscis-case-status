package com.twilio.uscis.casestatus

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.http.{Controller, HttpServer}

object TwilioApp extends TwilioServer

class TwilioServer extends HttpServer {

  override def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[CommonFilters]
      .add[TwilioController]
  }

}

class TwilioController extends Controller {

  post("/casestatus") { request: Request =>

    /** reading incoming parameters */
    // https://www.twilio.com/docs/messaging/guides/webhook-request#:~:text=When%20an%20incoming%20message%20arrives,sender%20and%20any%20attached%20media.
    val from: String = request.getParam("From").trim
    val to: String = request.getParam("To").trim
    val messageSid: String = request.getParam("MessageSid").trim
    val messageReceived: String = request.getParam("Body").trim

    logger.debug("received message From " + from + " To " + to + " , message SID is: " + messageSid +
      "message received is: " + messageReceived)

    // Omit dashes ("-") when entering a receipt number.
    val caseNumberWithOutDashes = validateCaseNumber.removeDashes(messageReceived).toUpperCase
    logger.debug("case number after dashes is removed " + caseNumberWithOutDashes)

    val message: String = validateCaseNumber.isCaseNumberValid(caseNumberWithOutDashes) match {
      case true => caseStatus.getCaseStatus(caseNumberWithOutDashes)
      case _ => "Thanks for sending me a message! please send a valid case number to receive case status."
    }

    val twiml = message

    twiml

  }

}
