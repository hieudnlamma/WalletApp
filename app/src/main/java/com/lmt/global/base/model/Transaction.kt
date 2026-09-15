package com.lmt.global.base.model

import androidx.annotation.DrawableRes
import java.math.BigDecimal

data class Transaction(
    val id: Long,
    val merchantName: String,
    val dateTime: String,
    val amount: BigDecimal,
    val currencyCode: String,
    @DrawableRes val merchantIconRes: Int,
    val type: String = TYPE_TRANSFER,
    val dueText: String? = null,
    val category: String? = null,
    val dueDate: String? = null,
    val registrationNumber: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
) {
    companion object {
        const val TYPE_TRANSFER = "TRANSFER"
        const val TYPE_PAY_BILL = "PAY_BILL"
    }
}
