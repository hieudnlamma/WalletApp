package com.lmt.global.base.view.bottom_sheet

import android.content.res.ColorStateList
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.lmt.global.base.R
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/** Bind the numeric amount, never infer its sign from a formatted currency string. */
@BindingAdapter(value = ["historyAmount", "historyCurrency"], requireAll = false)
fun bindHistoryAmount(view: TextView, amount: BigDecimal?, currencyCode: String?) {
    val sign = amount?.signum() ?: 0
    val (background, foreground) = when {
        sign < 0 -> R.color.hues_red_snow to R.color.hues_red_golden_gate_bridge
        sign > 0 -> R.color.hues_green_mint_cream to R.color.hues_green_sea_green
        else -> R.color.neutrals_cultured to R.color.neutrals_black_coral
    }
    view.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(view.context, background))
    view.setTextColor(ContextCompat.getColor(view.context, foreground))
    view.text = amount?.let {
        val currency = runCatching { Currency.getInstance(currencyCode ?: "USD") }
            .getOrDefault(Currency.getInstance("USD"))
        val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
            this.currency = currency
            minimumFractionDigits = currency.defaultFractionDigits.coerceAtLeast(0)
            maximumFractionDigits = minimumFractionDigits
        }
        (if (sign > 0) "+" else "") + formatter.format(it)
    }.orEmpty()
}
