package com.twilio.uscis.casestatus

object validateCaseNumber {

  /** remove dashes (-) */
  def removeDashes(caseNumber: String): String = {
    if (caseNumber != null && caseNumber.nonEmpty) {
      caseNumber.replace("-", "")
    } else {
      caseNumber
    }
  }

  /** validate case number as per uscis standards */
  def isCaseNumberValid(caseNumber: String): Boolean = {

    var caseNumberValid: Boolean = false

    // The receipt number is a unique 13-character
    if (caseNumber != null && caseNumber.nonEmpty && caseNumber.length == 13) {
      val caseNumberFirst3Chars = caseNumber.substring(0, 3)
      val caseNumberLast10Chars = caseNumber.substring(3)

      // The receipt number consists of three letters-for example, EAC, WAC, LIN, SRC, NBC, MSC or IOE-and 10 numbers.
      if (caseNumberFirst3Chars.forall(_.isLetter) && caseNumberLast10Chars.forall(_.isDigit)) {
        caseNumberValid = true
      }
    }
    caseNumberValid
  }

}
