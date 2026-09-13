package com.lmt.global.base.view.bottom_sheet

import android.util.Patterns
import androidx.databinding.DataBindingUtil
import com.lmt.global.base.R
import com.lmt.global.base.common.IBottomSheetDialogFragment
import com.lmt.global.base.databinding.BottomSheetForgotPasswordByEmailBinding
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.extension.showTemporaryError

class ForgotPasswordByEmailBottomSheet :
    IBottomSheetDialogFragment<BottomSheetForgotPasswordByEmailBinding>(
        { inflater, resource, root, attachToRoot ->
            DataBindingUtil.inflate(inflater, resource, root, attachToRoot)
        }
    ) {
    override fun provideLayout(): Int = R.layout.bottom_sheet_forgot_password_by_email

    private var onSendResetLinkClick: ((String) -> Unit)? = null
    private var onUseMobileInsteadClick: (() -> Unit)? = null

    override fun initViews() = Unit

    override fun initListeners() {
        binding.tvDone.onDebounceClick {
            dismiss()
        }
        binding.btnSendResetLink.onDebounceClick {
            val email = binding.edtEmail.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmail.showTemporaryError(getString(R.string.invalid_email))
                return@onDebounceClick
            }

            onSendResetLinkClick?.invoke(email)
            dismiss()
        }
        binding.tvUseMobileInstead.onDebounceClick {
            dismiss()
            onUseMobileInsteadClick?.invoke()
        }
    }

    fun setOnSendResetLinkClick(callback: (String) -> Unit): ForgotPasswordByEmailBottomSheet {
        onSendResetLinkClick = callback
        return this
    }

    fun setOnUseMobileInsteadClick(callback: () -> Unit): ForgotPasswordByEmailBottomSheet {
        onUseMobileInsteadClick = callback
        return this
    }

    companion object {
        fun newInstance(): ForgotPasswordByEmailBottomSheet {
            return ForgotPasswordByEmailBottomSheet()
        }
    }
}
