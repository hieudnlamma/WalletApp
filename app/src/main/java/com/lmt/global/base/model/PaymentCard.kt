package com.lmt.global.base.model

import androidx.annotation.ColorRes

data class PaymentCard(
    val id: Long,
    val cardHolder: String,
    val maskedNumber: String,
    val balance: String,
    @ColorRes val cardColorRes: Int,
    val lightCard: Boolean,
    val compactHeader: Boolean,
)
