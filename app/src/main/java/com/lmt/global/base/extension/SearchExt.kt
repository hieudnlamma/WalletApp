package com.lmt.global.base.extension

import com.lmt.global.base.model.Recipient
import com.lmt.global.base.model.Transaction
import java.util.Locale

fun Transaction.matchesSearchQuery(query: String): Boolean {
    val normalizedQuery = query.normalizedSearchQuery()
    if (normalizedQuery.isBlank()) return true

    return listOfNotNull(
        merchantName,
        dateTime,
        amount.toPlainString(),
        currencyCode,
        type,
        dueText,
        category,
        dueDate,
        registrationNumber,
    ).any { value -> value.normalizedSearchQuery().contains(normalizedQuery) }
}

fun Recipient.matchesSearchQuery(query: String): Boolean {
    val normalizedQuery = query.normalizedSearchQuery()
    if (normalizedQuery.isBlank()) return true
    if (name.normalizedSearchQuery().contains(normalizedQuery)) return true
    if (phoneNumber.normalizedSearchQuery().contains(normalizedQuery)) return true

    val compactQuery = normalizedQuery.filter(Char::isLetterOrDigit)
    val compactPhoneNumber = phoneNumber
        .normalizedSearchQuery()
        .filter(Char::isLetterOrDigit)
    return compactQuery.isNotBlank() && compactPhoneNumber.contains(compactQuery)
}

private fun String.normalizedSearchQuery(): String = trim().lowercase(Locale.ROOT)
