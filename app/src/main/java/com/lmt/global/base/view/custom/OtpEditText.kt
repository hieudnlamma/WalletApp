package com.lmt.global.base.view.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.lmt.global.base.R

class OtpEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle,
) : AppCompatEditText(
    context,
    attrs,
    defStyleAttr,
) {

    private val iconSize = 32.dp
    private val iconPadding = 8.dp

    private val otpDigits = StringBuilder()

    private val digitColor by lazy {
        ContextCompat.getColor(
            context,
            R.color.neutrals_black,
        )
    }

    private val placeholderColor by lazy {
        ContextCompat.getColor(
            context,
            R.color.neutrals_silver_sand,
        )
    }

    init {
        setupView()
        setNormalState()
    }

    private fun setupView() {
        // Bỏ background mặc định của EditText
        background = null

        typeface = ResourcesCompat.getFont(
            context,
            R.font.sora_regular,
        )

        setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            36f,
        )

        isSingleLine = true

        includeFontPadding = false

        gravity = Gravity.CENTER

        /*
         * Không sử dụng keyboard Android.
         * OTP sẽ được nhập từ NumberKeyboardView.
         */
        showSoftInputOnFocus = false

        /*
         * Không cho EditText tự nhận input từ keyboard.
         */
        keyListener = null

        /*
         * Không cần cursor vì sử dụng custom keyboard.
         */
        isCursorVisible = false

        compoundDrawablePadding = iconPadding

        setPadding(
            0,
            0,
            0,
            8.dp,
        )
    }

    /**
     * =========================
     * STATE
     * =========================
     */

    fun setNormalState() {
        setState(
            iconRes = null,
        )
    }

    fun setSuccessState() {
        setState(
            iconRes = R.drawable.icn_check_circle,
        )
    }

    fun setErrorState() {
        setState(
            iconRes = R.drawable.icn_error_circle,
        )
    }

    private fun setState(
        @DrawableRes iconRes: Int?,
    ) {
        setEndIcon(iconRes)

        renderOtp()

        invalidate()
    }

    /**
     * Cho phép truyền icon từ ngoài.
     */
    fun setEndIcon(
        @DrawableRes iconRes: Int?,
    ) {
        val drawable = iconRes?.let {
            createSizedDrawable(it)
        }

        setCompoundDrawables(
            null,
            null,
            drawable,
            null,
        )

        compoundDrawablePadding = iconPadding
        updateTextPadding(hasIcon = drawable != null)

        invalidate()
    }

    private fun updateTextPadding(
        hasIcon: Boolean,
    ) {
        val startPadding = if (hasIcon) {
            iconSize + iconPadding
        } else {
            0
        }

        setPadding(
            startPadding,
            paddingTop,
            0,
            paddingBottom,
        )
    }

    private fun createSizedDrawable(
        @DrawableRes drawableRes: Int,
    ): Drawable? {
        return ContextCompat.getDrawable(
            context,
            drawableRes,
        )?.apply {

            setBounds(
                0,
                0,
                iconSize,
                iconSize,
            )
        }
    }

    /**
     * =========================
     * OTP INPUT
     * =========================
     */

    fun appendDigit(
        digit: String,
    ) {
        if (otpDigits.length >= OTP_LENGTH) {
            return
        }

        val normalizedDigit = digit
            .singleOrNull()
            ?.takeIf {
                it.isDigit()
            }
            ?: return

        otpDigits.append(
            normalizedDigit,
        )

        /*
         * Khi user sửa OTP,
         * reset state về normal.
         */
        setNormalState()
    }

    fun deleteLastDigit() {
        if (otpDigits.isEmpty()) {
            return
        }

        otpDigits.deleteCharAt(
            otpDigits.lastIndex,
        )

        setNormalState()
    }

    fun clearOtp() {
        otpDigits.clear()

        setNormalState()
    }

    fun getOtp(): String {
        return otpDigits.toString()
    }

    fun isOtpComplete(): Boolean {
        return otpDigits.length == OTP_LENGTH
    }

    /**
     * =========================
     * OTP DISPLAY
     * =========================
     */

    private fun renderOtp() {
        val displayText = buildString {

            repeat(OTP_LENGTH) { index ->

                /*
                 * Thêm "-" trước digit thứ 4.
                 */
                if (index == OTP_SEPARATOR_INDEX) {
                    append(OTP_SEPARATOR)
                }

                /*
                 * Có digit -> show digit.
                 * Chưa có -> show X.
                 */
                append(
                    otpDigits.getOrNull(index)
                        ?: OTP_PLACEHOLDER,
                )
            }
        }

        val spannable = SpannableString(
            displayText,
        )

        displayText.forEachIndexed { index, character ->

            val color = when {

                /*
                 * Digit user đã nhập.
                 */
                character.isDigit() -> {
                    digitColor
                }

                /*
                 * Dấu "-"
                 *
                 * XXX-XXX
                 *    ↑ Silver Sand
                 *
                 * 123-XXX
                 *    ↑ vẫn Silver Sand
                 *
                 * 123-4XX
                 *    ↑ chuyển sang Black
                 */
                character == OTP_SEPARATOR -> {

                    if (otpDigits.length > OTP_SEPARATOR_INDEX) {
                        digitColor
                    } else {
                        placeholderColor
                    }
                }

                /*
                 * Các chữ X placeholder.
                 */
                else -> {
                    placeholderColor
                }
            }

            spannable.setSpan(
                ForegroundColorSpan(color),
                index,
                index + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
        }

        setText(
            spannable,
        )
    }

    private companion object {

        const val OTP_LENGTH = 6

        /*
         * 0 1 2 3 4 5
         * 1 2 3 - 4 5 6
         *
         * "-" nằm trước index 3.
         */
        const val OTP_SEPARATOR_INDEX = 3

        const val OTP_SEPARATOR = '-'

        const val OTP_PLACEHOLDER = 'X'
    }

    private val Int.dp: Int
        get() = (
                this * resources.displayMetrics.density
                ).toInt()
}
