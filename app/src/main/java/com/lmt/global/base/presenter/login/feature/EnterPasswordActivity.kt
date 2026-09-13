package com.lmt.global.base.presenter.login.feature

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.lmt.global.base.R
import com.lmt.global.base.common.CommonViewModel
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityEnterPasswordBinding
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.extension.startActivity
import com.lmt.global.base.extension.statusBars
import com.lmt.global.base.presenter.create_account.feature.InputOptActivity
import com.lmt.global.base.presenter.main.MainActivity
import com.lmt.global.base.view.bottom_sheet.ForgotPasswordByEmailBottomSheet
import com.lmt.global.base.view.bottom_sheet.ForgotPasswordByPhoneBottomSheet
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnterPasswordActivity : IActivity<ActivityEnterPasswordBinding, CommonViewModel>() {
    override fun provideViewModel() = viewModel<CommonViewModel>()
    override fun provideLayout() = R.layout.activity_enter_password

    override fun initViews(savedInstanceState: Bundle?) {
        setupInsets()
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
        setupApplyWindowInsetListener { insets ->
            viewBinding.toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.statusBars().top
            }
        }
        ViewCompat.requestApplyInsets(window.decorView)
    }

    private fun showForgotPasswordByEmailBottomSheet() {
        ForgotPasswordByEmailBottomSheet
            .newInstance()
            .setOnSendResetLinkClick { email ->
                openForgotPasswordOtp(email = email)
            }
            .setOnUseMobileInsteadClick {
                showForgotPasswordByPhoneBottomSheet()
            }
            .show(supportFragmentManager)
    }

    private fun showForgotPasswordByPhoneBottomSheet() {
        ForgotPasswordByPhoneBottomSheet
            .newInstance()
            .setOnSendResetLinkClick { phoneNumber ->
                openForgotPasswordOtp(phoneNumber = phoneNumber)
            }
            .setOnUseEmailInsteadClick {
                showForgotPasswordByEmailBottomSheet()
            }
            .show(supportFragmentManager)
    }

    private fun openForgotPasswordOtp(
        email: String? = null,
        phoneNumber: String? = null,
    ) {
        startActivity(
            Intent(this, InputOptActivity::class.java).apply {
                putExtra(InputOptActivity.EXTRA_FLOW, InputOptActivity.FLOW_FORGOT_PASSWORD)
                email?.let { putExtra(InputOptActivity.EXTRA_EMAIL, it) }
                phoneNumber?.let { putExtra(InputOptActivity.EXTRA_PHONE_NUMBER, it) }
            }
        )
    }

    companion object {
        const val EXTRA_PHONE_NUMBER = "extra_phone_number"
    }
}
