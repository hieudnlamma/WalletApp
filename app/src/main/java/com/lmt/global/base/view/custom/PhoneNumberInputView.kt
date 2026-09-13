package com.lmt.global.base.view.custom

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.hbb20.CountryCodePicker
import com.lmt.global.base.R
import com.lmt.global.base.extension.dpToPx
import com.lmt.global.base.extension.clearTemporaryError
import com.lmt.global.base.extension.hideKeyboard
import com.lmt.global.base.extension.showKeyboard
import com.lmt.global.base.extension.showTemporaryError
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import java.util.Locale

class PhoneNumberInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val countrySelector: View
    private val countryFlag: ImageView
    private val countryCode: TextView
    private val countryDropdownIcon: ImageView
    private val phoneNumberInput: EditText
    private val countryCodePicker: CountryCodePicker

    private var countryDropdown: ListPopupWindow? = null
    private val countryPhoneCodes by lazy(::createCountryPhoneCodes)

    val phoneNumberOrNull: String?
        get() = if (phoneNumberInput.text?.isNotBlank() == true &&
            countryCodePicker.isValidFullNumber
        ) {
            countryCodePicker.fullNumberWithPlus
        } else {
            null
        }

    val selectedCountryName: String
        get() = countryCodePicker.selectedCountryName

    val isPhoneInputFocused: Boolean
        get() = phoneNumberInput.hasFocus()

    init {
        LayoutInflater.from(context).inflate(R.layout.view_phone_number_input, this, true)
        countrySelector = findViewById(R.id.countrySelector)
        countryFlag = findViewById(R.id.ivCountryFlag)
        countryCode = findViewById(R.id.tvCountryCode)
        countryDropdownIcon = findViewById(R.id.ivCountryDropdown)
        phoneNumberInput = findViewById(R.id.edtPhoneNumber)
        countryCodePicker = findViewById(R.id.countryCodePickerLogic)

        setupCountryCodePicker()
        countrySelector.setOnClickListener { showOrDismissCountryDropdown() }
    }

    fun showValidationError(message: CharSequence) {
        phoneNumberInput.showTemporaryError(message)
        requestPhoneInputFocus()
    }

    fun requestPhoneInputFocus() {
        phoneNumberInput.showKeyboard()
    }

    fun setOnPhoneFocusChangeListener(listener: (hasFocus: Boolean) -> Unit) {
        phoneNumberInput.setOnFocusChangeListener { _, hasFocus -> listener(hasFocus) }
    }

    private fun setupCountryCodePicker() {
        countryCodePicker.apply {
            registerCarrierNumberEditText(phoneNumberInput)
            setDefaultCountryUsingNameCode(DEFAULT_COUNTRY_NAME_CODE)
            resetToDefaultCountry()
            syncCountryUi()
            setOnCountryChangeListener { syncCountryUi() }
            setPhoneNumberValidityChangeListener { isValid ->
                if (isValid) phoneNumberInput.clearTemporaryError()
            }
        }
    }

    private fun syncCountryUi() {
        countryFlag.setImageResource(countryCodePicker.selectedCountryFlagResourceId)
        countryCode.text = countryCodePicker.selectedCountryCodeWithPlus
        countrySelector.contentDescription =
            "${countryCodePicker.selectedCountryName}, " +
            countryCodePicker.selectedCountryCodeWithPlus
    }

    private fun showOrDismissCountryDropdown() {
        if (countryDropdown?.isShowing == true) {
            countryDropdown?.dismiss()
            return
        }

        phoneNumberInput.hideKeyboard()
        phoneNumberInput.clearFocus()
        postDelayed({ showCountryDropdown() }, KEYBOARD_DISMISS_DELAY_MILLIS)
    }

    private fun showCountryDropdown() {
        if (!isAttachedToWindow) return

        val anchorLocation = IntArray(2).also(::getLocationOnScreen)
        val visibleWindow = Rect().also(::getWindowVisibleDisplayFrame)
        val availableHeight = visibleWindow.bottom - anchorLocation[1] - height -
            DROPDOWN_SCREEN_MARGIN_DP.dpToPx.toInt() -
            DROPDOWN_VERTICAL_OFFSET_DP.dpToPx.toInt()
        val dropdownHeight = minOf(
            DROPDOWN_MAX_HEIGHT_DP.dpToPx.toInt(),
            availableHeight.coerceAtLeast(1),
        )
        val countryAdapter = CountryPhoneCodeAdapter(context, countryPhoneCodes)
        val searchView = LayoutInflater.from(context)
            .inflate(R.layout.view_country_phone_search, this, false)
        searchView.findViewById<EditText>(R.id.edtSearchCountry).doAfterTextChanged { query ->
            countryAdapter.filter(query)
        }

        countryDropdown = ListPopupWindow(context).apply {
            anchorView = this@PhoneNumberInputView
            setAdapter(countryAdapter)
            setPromptPosition(ListPopupWindow.POSITION_PROMPT_ABOVE)
            setPromptView(searchView)
            width = this@PhoneNumberInputView.width
            height = dropdownHeight
            verticalOffset = DROPDOWN_VERTICAL_OFFSET_DP.dpToPx.toInt()
            isModal = true
            inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
            setDropDownGravity(Gravity.START)
            setBackgroundDrawable(
                ContextCompat.getDrawable(context, R.drawable.bg_country_dropdown)
            )
            setOnItemClickListener { _, _, position, _ ->
                countryCodePicker.setCountryForNameCode(
                    countryAdapter.getItem(position).regionCode
                )
                syncCountryUi()
                dismiss()
            }
            setOnDismissListener {
                countryDropdownIcon.animate().rotation(0f).setDuration(ARROW_ANIMATION_MILLIS).start()
            }
            show()

            listView?.apply {
                divider = ColorDrawable(
                    ContextCompat.getColor(context, R.color.neutrals_alice_blue)
                )
                dividerHeight = 1.dpToPx.toInt()
                elevation = DROPDOWN_ELEVATION_DP.dpToPx
                isVerticalScrollBarEnabled = true
            }
        }
        countryDropdownIcon.animate()
            .rotation(180f)
            .setDuration(ARROW_ANIMATION_MILLIS)
            .start()
    }

    private fun createCountryPhoneCodes(): List<CountryPhoneCode> {
        val phoneNumberUtil = PhoneNumberUtil.createInstance(context)
        val flagPicker = CountryCodePicker(context)
        val displayLocale = resources.configuration.locales[0]

        return Locale.getISOCountries().mapNotNull { regionCode ->
            val dialingCode = phoneNumberUtil.getCountryCodeForRegion(regionCode)
            if (dialingCode == 0) return@mapNotNull null
            flagPicker.setCountryForNameCode(regionCode)

            CountryPhoneCode(
                regionCode = regionCode,
                countryName = Locale("", regionCode).getDisplayCountry(displayLocale)
                    .ifBlank { regionCode },
                phoneCode = "+$dialingCode",
                flagResourceId = flagPicker.selectedCountryFlagResourceId,
            )
        }.sortedBy(CountryPhoneCode::countryName)
    }

    override fun onDetachedFromWindow() {
        countryDropdown?.dismiss()
        countryDropdown = null
        super.onDetachedFromWindow()
    }

    private companion object {
        const val DEFAULT_COUNTRY_NAME_CODE = "JO"
        const val DROPDOWN_MAX_HEIGHT_DP = 280
        const val DROPDOWN_SCREEN_MARGIN_DP = 16
        const val DROPDOWN_VERTICAL_OFFSET_DP = 4
        const val DROPDOWN_ELEVATION_DP = 8
        const val KEYBOARD_DISMISS_DELAY_MILLIS = 180L
        const val ARROW_ANIMATION_MILLIS = 150L
    }
}
