package com.lmt.global.base.view.bottom_sheet

import androidx.databinding.DataBindingUtil
import com.lmt.global.base.R
import com.lmt.global.base.common.IBottomSheetDialogFragment
import com.lmt.global.base.databinding.BottomSheetForgotPasswordByPhoneBinding
import com.lmt.global.base.extension.onDebounceClick

class ForgotPasswordByPhoneBottomSheet :
    IBottomSheetDialogFragment<BottomSheetForgotPasswordByPhoneBinding>(
        { inflater, resource, root, attachToRoot ->
            DataBindingUtil.inflate(inflater, resource, root, attachToRoot)
        }
    ) {
    override fun provideLayout(): Int = R.layout.bottom_sheet_forgot_password_by_phone

    private var onSendResetLinkClick: ((countryCode: String, mobileNumber: String) -> Unit)? = null
    private var onUseEmailInsteadClick: (() -> Unit)? = null

    override fun initViews() = Unit

    override fun initListeners() {
        binding.tvDone.onDebounceClick {
            dismiss()
        }
        binding.btnSendResetLink.onDebounceClick {
            onSendResetLinkClick?.invoke(
                binding.tvCountryCode.text.toString(),
                binding.edtMobileNumber.text.toString().trim(),
            )
            dismiss()
        }
        binding.tvUseEmailInstead.onDebounceClick {
            dismiss()
            onUseEmailInsteadClick?.invoke()
        }
    }

    fun setOnSendResetLinkClick(
        callback: (countryCode: String, mobileNumber: String) -> Unit
    ): ForgotPasswordByPhoneBottomSheet {
        onSendResetLinkClick = callback
        return this
    }

    fun setOnUseEmailInsteadClick(callback: () -> Unit): ForgotPasswordByPhoneBottomSheet {
        onUseEmailInsteadClick = callback
        return this
    }

    companion object {
        fun newInstance(): ForgotPasswordByPhoneBottomSheet {
            return ForgotPasswordByPhoneBottomSheet()
        }
    }
}
