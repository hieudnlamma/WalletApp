package com.lmt.global.base.presenter.login.feature

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.lmt.global.base.R
import com.lmt.global.base.common.CommonViewModel
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityEnterPasswordBinding
import com.lmt.global.base.extension.TopNotificationType
import com.lmt.global.base.extension.collectLatestRepeatOnLifecycle
import com.lmt.global.base.extension.collectRepeatOnLifecycle
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.extension.showTemporaryError
import com.lmt.global.base.extension.showTopNotification
import com.lmt.global.base.extension.startActivity
import com.lmt.global.base.extension.statusBars
import com.lmt.global.base.presenter.create_account.feature.InputOptActivity
import com.lmt.global.base.presenter.main.MainActivity
import com.lmt.global.base.view.bottom_sheet.ForgotPasswordByEmailBottomSheet
import com.lmt.global.base.view.bottom_sheet.ForgotPasswordByPhoneBottomSheet
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnterPasswordActivity : IActivity<ActivityEnterPasswordBinding, EnterPasswordViewModel>() {
    override fun provideViewModel() = viewModel<EnterPasswordViewModel>()
    override fun provideLayout() = R.layout.activity_enter_password

    override fun initViews(savedInstanceState: Bundle?) {
        setupInsets()
    }

    override fun initListeners() {
        viewBinding.btnLogin.onDebounceClick {
            viewModel.onState(
                EnterPasswordAction.Login(
                    intent
                        .getStringExtra(EXTRA_PHONE_NUMBER)
                        .orEmpty(),
                    viewBinding.edtPassword
                        .text
                        .toString()
                        .toCharArray(),
                )
            )
        }

        viewBinding.tvForgotPassword.onDebounceClick {
            showForgotPasswordByEmailBottomSheet()
        }
    }

    override fun initObservers() {
        super.initObservers()

        collectLatestRepeatOnLifecycle(
            flow = viewModel.uiState,
        ) { state ->
            renderLoginState(state)
        }

        collectRepeatOnLifecycle(
            flow = viewModel.effects,
        ) { effect ->
            handleLoginEffect(effect)
        }
    }

    private fun renderLoginState(
        state: EnterPasswordUiState,
    ) {
        viewBinding.btnLogin.isEnabled = !state.isBusy
        viewBinding.edtPassword.isEnabled = !state.isBusy
        viewBinding.tvForgotPassword.isEnabled = !state.isBusy

        viewBinding.progressLogin.isVisible = state.isLoading

        viewBinding.btnLogin.text =
            if (state.isLoading) {
                ""
            } else {
                getString(R.string.login)
            }
    }

    private fun handleLoginEffect(
        effect: EnterPasswordEffect,
    ) {
        when (effect) {
            EnterPasswordEffect.PasswordRequired -> {
                viewBinding.edtPassword.showTemporaryError(
                    getString(R.string.password_required)
                )
            }

            EnterPasswordEffect.IncorrectPassword -> {
                viewBinding.edtPassword.showTemporaryError(
                    getString(R.string.incorrect_password)
                )
            }

            EnterPasswordEffect.LoginFailed -> {
                showTopNotification(
                    message = getString(R.string.login_failed),
                    type = TopNotificationType.ERROR,
                )
            }

            is EnterPasswordEffect.LoginSucceeded -> {
                appSharedPreferences.currentUserPhoneNumber =
                    effect.phoneNumber

                startActivity<MainActivity> {
                    flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
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
