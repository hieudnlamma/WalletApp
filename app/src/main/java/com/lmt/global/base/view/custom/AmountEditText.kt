package com.lmt.global.base.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.os.SystemClock
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.lmt.global.base.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/** Whole-unit amount entered from NumberKeyboardView and displayed with two decimals. */
class AmountEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle,
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private var integerDigits = ""
    private var decimalDigits = ""
    private var decimalMode = false
    private val cursorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.neutrals_black)
        strokeWidth = resources.getDimension(R.dimen._2dp)
    }
    private val cursorGap = resources.getDimensionPixelSize(R.dimen._4dp)
    private val cursorHeight = resources.getDimension(R.dimen._38dp)
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
        currency = Currency.getInstance("USD")
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    private val decimalSeparator = '.'

    init {
        background = null
        typeface = ResourcesCompat.getFont(context, R.font.sora_regular)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen._36sp))
        setSingleLine(true)
        includeFontPadding = false
        gravity = Gravity.CENTER
        hint = context.getString(R.string.amount_placeholder)
        setHintTextColor(ContextCompat.getColor(context, R.color.neutrals_silver_sand))
        setTextColor(ContextCompat.getColor(context, R.color.neutrals_black))
        showSoftInputOnFocus = false
        keyListener = null
        isCursorVisible = false
        isLongClickable = false
        setTextIsSelectable(false)
        isFocusableInTouchMode = true
        // Match OtpEditText sizing; the screen owns the separate underline.
        setPadding(0, 0, 0, resources.getDimensionPixelSize(R.dimen._8dp))
        minHeight = resources.getDimensionPixelSize(R.dimen._54dp)
        minimumHeight = resources.getDimensionPixelSize(R.dimen._54dp)
        isHorizontalScrollBarEnabled = false
        isVerticalScrollBarEnabled = false
        setHorizontallyScrolling(false)
        backgroundTintList = null
        background = null
        contentDescription = context.getString(R.string.enter_amount)
        renderAmount()
    }

    fun appendDigit(digit: String) {
        if (digit == decimalSeparator.toString()) {
            if (!decimalMode) {
                if (integerDigits.isEmpty()) integerDigits = "0"
                decimalMode = true
                renderAmount()
            }
            return
        }
        if (digit.length != 1 || digit[0] !in '0'..'9') return

        if (decimalMode) {
            if (decimalDigits.length >= DECIMAL_PLACES) return
            decimalDigits += digit
        } else {
            val next = (integerDigits + digit).trimStart('0').ifEmpty { "0" }
            if (next.length > MAX_INTEGER_DIGITS) return
            integerDigits = next
        }
        renderAmount()
    }

    fun deleteLastDigit() {
        when {
            decimalDigits.isNotEmpty() -> decimalDigits = decimalDigits.dropLast(1)
            decimalMode -> decimalMode = false
            else -> integerDigits = integerDigits.dropLast(1)
        }
        renderAmount()
    }

    fun clearAmount() {
        integerDigits = ""
        decimalDigits = ""
        decimalMode = false
        renderAmount()
    }

    fun getAmount(): BigDecimal {
        val whole = integerDigits.ifEmpty { "0" }
        val fraction = decimalDigits.padEnd(DECIMAL_PLACES, '0')
        return BigDecimal("$whole.$fraction")
    }

    fun setAmount(amount: BigDecimal) {
        require(amount.signum() >= 0) { "Amount must not be negative" }
        val scaledAmount = amount.setScale(DECIMAL_PLACES, RoundingMode.UNNECESSARY)
        val parts = scaledAmount.toPlainString().split(decimalSeparator)
        val wholeUnits = parts.first().trimStart('0').ifEmpty { "0" }
        require(wholeUnits.length <= MAX_INTEGER_DIGITS) {
            "Amount exceeds $MAX_INTEGER_DIGITS whole-number digits"
        }
        integerDigits = wholeUnits
        decimalDigits = parts.getOrElse(1) { "" }.trimEnd('0')
        decimalMode = decimalDigits.isNotEmpty()
        renderAmount()
    }

    private fun renderAmount() {
        setText(
            if (integerDigits.isEmpty()) {
                ""
            } else {
                val formattedAmount = currencyFormatter.format(getAmount())
                val separatorIndex = formattedAmount.lastIndexOf(decimalSeparator)
                val integerPrefix = if (separatorIndex >= 0) {
                    formattedAmount.substring(0, separatorIndex)
                } else {
                    formattedAmount
                }
                if (decimalMode) {
                    "$integerPrefix$decimalSeparator$decimalDigits"
                } else {
                    "$integerPrefix${decimalSeparator}00"
                }
            },
        )
        setSelection(text?.length ?: 0)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Match the design's 38dp caret without opening the system keyboard.
        if (!isFocused && !isInEditMode) return
        if (!isInEditMode && SystemClock.uptimeMillis() % CURSOR_BLINK_PERIOD >= CURSOR_VISIBLE_TIME) {
            postInvalidateDelayed(CURSOR_VISIBLE_TIME)
            return
        }
        val displayText = if (integerDigits.isEmpty()) hint?.toString().orEmpty() else text.toString()
        val textWidth = paint.measureText(displayText)
        val left = (width - textWidth) / 2f
        val x = when {
            integerDigits.isEmpty() -> left - cursorGap / 2f
            decimalMode -> left + textWidth + cursorGap / 2f
            else -> {
                val separatorIndex = displayText.lastIndexOf(decimalSeparator)
                val caretIndex = if (separatorIndex >= 0) separatorIndex else displayText.length
                val integerWidth = paint.measureText(
                    displayText.substring(0, caretIndex),
                )
                left + integerWidth - cursorGap / 2f
            }
        }
        canvas.drawLine(x, (height - cursorHeight) / 2f,
            x, (height + cursorHeight) / 2f, cursorPaint)
        if (!isInEditMode) postInvalidateDelayed(CURSOR_VISIBLE_TIME)
    }

    override fun onSaveInstanceState(): Parcelable = Bundle().apply {
        putParcelable("parent", super.onSaveInstanceState())
        putString(STATE_INTEGER_DIGITS, integerDigits)
        putString(STATE_DECIMAL_DIGITS, decimalDigits)
        putBoolean(STATE_DECIMAL_MODE, decimalMode)
    }

    @Suppress("DEPRECATION")
    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable("parent"))
            integerDigits = state.getString(STATE_INTEGER_DIGITS).orEmpty()
            decimalDigits = state.getString(STATE_DECIMAL_DIGITS).orEmpty()
            decimalMode = state.getBoolean(STATE_DECIMAL_MODE)
            renderAmount()
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private companion object {
        const val CURSOR_BLINK_PERIOD = 1_000L
        const val CURSOR_VISIBLE_TIME = 500L
        const val DECIMAL_PLACES = 2
        const val MAX_INTEGER_DIGITS = 12
        const val STATE_INTEGER_DIGITS = "integer_digits"
        const val STATE_DECIMAL_DIGITS = "decimal_digits"
        const val STATE_DECIMAL_MODE = "decimal_mode"
    }
}
