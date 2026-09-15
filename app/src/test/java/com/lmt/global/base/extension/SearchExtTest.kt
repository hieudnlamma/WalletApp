package com.lmt.global.base.extension

import com.lmt.global.base.model.Recipient
import com.lmt.global.base.model.Transaction
import java.math.BigDecimal
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchExtTest {
    @Test
    fun transactionSearch_matchesRelevantFieldsIgnoringCase() {
        val transaction = Transaction(
            id = 1L,
            merchantName = "Electricity",
            dateTime = "December 29, 2022 - 12:32",
            amount = BigDecimal("132.32"),
            currencyCode = "USD",
            merchantIconRes = 0,
            category = "Utility",
            registrationNumber = "00000000000001",
        )

        assertTrue(transaction.matchesSearchQuery("electric"))
        assertTrue(transaction.matchesSearchQuery("UTILITY"))
        assertTrue(transaction.matchesSearchQuery("132.32"))
        assertTrue(transaction.matchesSearchQuery("000001"))
        assertFalse(transaction.matchesSearchQuery("internet"))
    }

    @Test
    fun recipientSearch_matchesNameAndCompactPhoneNumber() {
        val recipient = Recipient(
            id = 1L,
            name = "Ali Ahmed",
            phoneNumber = "+1 300-555-0161",
            avatarRes = 0,
        )

        assertTrue(recipient.matchesSearchQuery("ALI"))
        assertTrue(recipient.matchesSearchQuery("13005550161"))
        assertFalse(recipient.matchesSearchQuery("Abdullah"))
    }
}
