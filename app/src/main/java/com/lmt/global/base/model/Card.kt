package com.lmt.global.base.model

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.lmt.global.base.R
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card(
    val id: String,
    val name: String,
    val cardNumber: String,
    val balanceMinor: Long,
    val createdAt: Long,
    @param:ColorRes val cardColorRes: Int = R.color.hues_purple_indigo,
    val lightCard: Boolean = false,
    val compactHeader: Boolean = false,
) : Parcelable {
    val cardHolder: String
        get() = name

    val maskedNumber: String
        get() = "**** ${cardNumber.takeLast(CARD_NUMBER_SUFFIX_LENGTH)}"

    val balance: String
        get() = NumberFormat.getCurrencyInstance(Locale.US).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 2
        }.format(BigDecimal.valueOf(balanceMinor, MINOR_UNIT_SCALE))

    private companion object {
        const val CARD_NUMBER_SUFFIX_LENGTH = 4
        const val MINOR_UNIT_SCALE = 2
    }
}
