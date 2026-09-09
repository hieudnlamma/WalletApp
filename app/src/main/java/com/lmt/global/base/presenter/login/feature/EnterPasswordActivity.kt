package com.lmt.global.base.presenter.login.feature

import android.content.Intent
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.lmt.global.base.R
import com.lmt.global.base.common.CommonViewModel
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityEnterPasswordBinding
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.extension.startActivity
import com.lmt.global.base.presenter.main.MainActivity
import com.lmt.global.base.view.bottom_sheet.ForgotPasswordByEmailBottomSheet
import com.lmt.global.base.view.bottom_sheet.ForgotPasswordByPhoneBottomSheet
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnterPasswordActivity : IActivity<ActivityEnterPasswordBinding, CommonViewModel>() {
    override fun provideViewModel() = viewModel<CommonViewModel>()
    override fun provideLayout() = R.layout.activity_enter_password

    override fun initViews(savedInstanceState: Bundle?) {
        setupInsets()
        setupKeyboardScroll()
    }

    override fun initListeners() {
        viewBinding.btnLogin.onDebounceClick {
            startActivity<MainActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        viewBinding.tvForgotPassword.onDebounceClick {
            showForgotPasswordByEmailBottomSheet()
        }
    }

    private fun setupInsets() {
        val toolbarPaddingTop = viewBinding.toolbar.paddingTop
        val scrollPaddingBottom = viewBinding.passwordScrollView.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { _, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navigationBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            viewBinding.toolbar.updatePadding(top = toolbarPaddingTop + statusBar)
            viewBinding.passwordScrollView.updatePadding(
                bottom = scrollPaddingBottom + maxOf(ime, navigationBar)
            )

            if (ime > 0 && viewBinding.edtPassword.hasFocus()) {
                scrollPasswordInputAboveKeyboard()
            }

            insets
        }
        ViewCompat.requestApplyInsets(viewBinding.root)
    }

    private fun setupKeyboardScroll() {
        viewBinding.edtPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                scrollPasswordInputAboveKeyboard()
            }
        }
    }

    private fun scrollPasswordInputAboveKeyboard() {
        viewBinding.passwordScrollView.post {
            val targetBottom = viewBinding.passwordInputContainer.bottom
            val visibleBottom =
                viewBinding.passwordScrollView.height - viewBinding.passwordScrollView.paddingBottom
            val scrollY = (targetBottom - visibleBottom).coerceAtLeast(0)
            viewBinding.passwordScrollView.smoothScrollTo(0, scrollY)
        }
    }

    private fun showForgotPasswordByEmailBottomSheet() {
        ForgotPasswordByEmailBottomSheet
            .newInstance()
            .setOnSendResetLinkClick { email ->
                handleForgotPasswordByEmail(email)
            }
            .setOnUseMobileInsteadClick {
                showForgotPasswordByPhoneBottomSheet()
            }
            .show(supportFragmentManager)
    }

    private fun showForgotPasswordByPhoneBottomSheet() {
        ForgotPasswordByPhoneBottomSheet
            .newInstance()
            .setOnSendResetLinkClick { countryCode, mobileNumber ->
                handleForgotPasswordByPhone(countryCode, mobileNumber)
            }
            .setOnUseEmailInsteadClick {
                showForgotPasswordByEmailBottomSheet()
            }
            .show(supportFragmentManager)
    }

    private fun handleForgotPasswordByEmail(email: String) = Unit

    private fun handleForgotPasswordByPhone(countryCode: String, mobileNumber: String) = Unit
}
