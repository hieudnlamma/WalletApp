package com.lmt.global.base.presenter.login

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import com.lmt.global.base.R
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityLoginBinding
import com.lmt.global.base.extension.TopNotificationType
import com.lmt.global.base.extension.collectLatestRepeatOnLifecycle
import com.lmt.global.base.extension.showTopNotification
import com.lmt.global.base.extension.statusBars
import com.lmt.global.base.presenter.create_account.CreateAccountActivity
import com.lmt.global.base.presenter.login.feature.EnterPasswordActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : IActivity<ActivityLoginBinding, LoginViewModel>() {

    override fun provideViewModel() = viewModel<LoginViewModel>()

    override fun provideLayout() = R.layout.activity_login

    override fun initViews(savedInstanceState: Bundle?) {
        setupInsets()
    }

    override fun initListeners() {
        viewBinding.btnContinue.setOnClickListener {
            validatedPhoneNumber()?.let(viewModel::checkRegistration)
        }

        viewBinding.tvCreateAccount.setOnClickListener {
            openCreateAccount()
        }
    }

    override fun initObservers() {
        super.initObservers()
        collectLatestRepeatOnLifecycle(flow = viewModel.results) { result ->
            handleLoginResult(result)
        }
    }

    private fun validatedPhoneNumber(): String? {
        val phoneNumber = viewBinding.mobileInputContainer.phoneNumberOrNull
        if (phoneNumber == null) {
            viewBinding.mobileInputContainer.showValidationError(
                getString(
                    R.string.invalid_phone_number,
                    viewBinding.mobileInputContainer.selectedCountryName,
                )
            )
        }
        return phoneNumber
    }

    private fun handleLoginResult(result: LoginResult) {
        when (result) {
            is LoginResult.Registered -> {
                startActivity(
                    Intent(this, EnterPasswordActivity::class.java).putExtra(
                        EnterPasswordActivity.EXTRA_PHONE_NUMBER,
                        result.user.phoneNumber,
                    )
                )
            }

            LoginResult.NotRegistered -> {
                showTopNotification(
                    message = getString(R.string.user_not_registered),
                    type = TopNotificationType.WARNING,
                    actionText = getString(R.string.create_account),
                    onAction = ::openCreateAccount,
                )
            }

            is LoginResult.Error -> {
                showTopNotification(
                    message = getString(R.string.unable_to_check_user),
                    type = TopNotificationType.ERROR,
                )
            }
        }
    }

    private fun openCreateAccount() {
        startActivity(Intent(this, CreateAccountActivity::class.java))
    }

    private fun setupInsets() {
        setupApplyWindowInsetListener { insets ->
            viewBinding.toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.statusBars().top
            }
        }
        ViewCompat.requestApplyInsets(window.decorView)
    }
}
