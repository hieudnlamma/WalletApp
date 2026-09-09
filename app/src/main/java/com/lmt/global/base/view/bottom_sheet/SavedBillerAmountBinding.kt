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

@BindingAdapter(value = ["savedBillerAmount", "savedBillerCurrency"], requireAll = false)
fun bindSavedBillerAmount(view: TextView, amount: BigDecimal?, currencyCode: String?) {
    val sign = amount?.signum() ?: 0
    val (backgroundColor, textColor) = when {
        sign < 0 -> R.color.hues_red_snow to R.color.hues_red_golden_gate_bridge
        sign > 0 -> R.color.hues_green_mint_cream to R.color.hues_green_sea_green
        else -> R.color.neutrals_cultured to R.color.neutrals_black_coral
    }

    view.backgroundTintList = ColorStateList.valueOf(
        ContextCompat.getColor(view.context, backgroundColor),
    )
    view.setTextColor(ContextCompat.getColor(view.context, textColor))
    view.text = amount?.let {
        val currency = runCatching { Currency.getInstance(currencyCode ?: DEFAULT_CURRENCY_CODE) }
            .getOrDefault(Currency.getInstance(DEFAULT_CURRENCY_CODE))
        val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
            this.currency = currency
            minimumFractionDigits = currency.defaultFractionDigits.coerceAtLeast(0)
            maximumFractionDigits = minimumFractionDigits
        }
        view.context.getString(R.string.due_amount, formatter.format(it.abs()))
    }.orEmpty()
}

private const val DEFAULT_CURRENCY_CODE = "USD"
