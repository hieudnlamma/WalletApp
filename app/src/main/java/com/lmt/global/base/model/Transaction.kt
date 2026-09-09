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
)
