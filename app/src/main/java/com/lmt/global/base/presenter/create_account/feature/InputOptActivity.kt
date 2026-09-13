package com.lmt.global.base.presenter.create_account.feature

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.lmt.global.base.R
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityInputOptBinding
import com.lmt.global.base.extension.TopNotificationType
import com.lmt.global.base.extension.collectLatestRepeatOnLifecycle
import com.lmt.global.base.extension.collectRepeatOnLifecycle
import com.lmt.global.base.extension.showTopNotification
import com.lmt.global.base.presenter.login.LoginActivity
import com.lmt.global.base.presenter.login.feature.EnterNewPasswordActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class InputOptActivity : IActivity<ActivityInputOptBinding, InputOtpViewModel>() {

    override fun provideViewModel() = viewModel<InputOtpViewModel>()

    override fun provideLayout() = R.layout.activity_input_opt

    override fun initViews(savedInstanceState: Bundle?) {
        setupInsets()
        setupOtpInput()
        showDestinationPhoneNumber()
        viewModel.onState(InputOtpAction.StartCountdown)
    }

    override fun initListeners() = with(viewBinding) {
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        numberKeyboard.setOnNumberClickListener { number ->
            viewModel.onState(InputOtpAction.ResetOtpState)
            edtOtp.appendDigit(number)
        }

        numberKeyboard.setOnDeleteClickListener {
            viewModel.onState(InputOtpAction.ResetOtpState)
            edtOtp.deleteLastDigit()
        }

        llResentCode.setOnClickListener {
            viewModel.onState(InputOtpAction.ResendCode)
        }

        btnDone.setOnClickListener {
            verifyOtp()
        }
    }

    override fun initObservers() {
        super.initObservers()

        collectLatestRepeatOnLifecycle(flow = viewModel.uiState) { state ->
            renderUiState(state)
        }
        collectRepeatOnLifecycle(flow = viewModel.effects) { effect ->
            handleEffect(effect)
        }
    }

    private fun showDestinationPhoneNumber() {
        val destination = intent.getStringExtra(EXTRA_PHONE_NUMBER)
            ?: intent.getStringExtra(EXTRA_EMAIL)
            ?: return
        viewBinding.tvLabel.text = getString(R.string.verification_code_sent_to, destination)
    }

    private fun verifyOtp() {
        val phoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER)
        val email = intent.getStringExtra(EXTRA_EMAIL)
        if (phoneNumber == null && email == null) return

        viewModel.onState(
            InputOtpAction.VerifyOtp(
                otp = viewBinding.edtOtp.getOtp(),
                phoneNumber = phoneNumber,
                fullName = intent.getStringExtra(EXTRA_FULL_NAME),
                email = email,
                isForgotPassword = intent.getStringExtra(EXTRA_FLOW) == FLOW_FORGOT_PASSWORD,
            )
        )
    }

    private fun renderUiState(state: InputOtpUiState) {
        renderCountdown(state)
        renderOtpStatus(state.otpStatus)
        viewBinding.btnDone.isEnabled = !state.isSavingUser
    }

    private fun renderCountdown(state: InputOtpUiState) {
        viewBinding.tvCountdown.text = formatCountdown(state.secondsRemaining)
        val colorRes = if (state.isResendEnabled) {
            R.color.hues_blue_celtic_blue
        } else {
            R.color.neutrals_slate_gray
        }
        viewBinding.tvResentCode.setTextColor(ContextCompat.getColor(this, colorRes))
    }

    private fun renderOtpStatus(status: OtpStatus) {
        when (status) {
            OtpStatus.NORMAL -> setOtpState(
                colorRes = R.color.hues_purple_ocean_blue,
                updateOtpView = viewBinding.edtOtp::setNormalState,
            )

            OtpStatus.SUCCESS -> setOtpState(
                colorRes = R.color.hues_green_shamrock,
                updateOtpView = viewBinding.edtOtp::setSuccessState,
            )

            OtpStatus.ERROR -> setOtpState(
                colorRes = R.color.hues_red_golden_gate_bridge,
                updateOtpView = viewBinding.edtOtp::setErrorState,
            )
        }
    }

    private fun setOtpState(
        @ColorRes colorRes: Int,
        updateOtpView: () -> Unit,
    ) {
        updateOtpView()
        viewBinding.viewOtpUnderline.setBackgroundColor(
            ContextCompat.getColor(this, colorRes)
        )
    }

    private fun handleEffect(effect: InputOtpEffect) {
        when (effect) {
            InputOtpEffect.ClearOtp -> viewBinding.edtOtp.clearOtp()

            InputOtpEffect.ShowInvalidOtpLength -> Toast.makeText(
                this,
                R.string.enter_six_digit_code,
                Toast.LENGTH_SHORT,
            ).show()

            InputOtpEffect.AccountCreated -> {
                showTopNotification(
                    message = getString(R.string.account_created),
                    type = TopNotificationType.SUCCESS,
                )

                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                startActivity(intent)
            }

            InputOtpEffect.PasswordResetVerified -> {
                startActivity(Intent(this, EnterNewPasswordActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }

            InputOtpEffect.SaveUserFailed -> showTopNotification(
                message = getString(R.string.unable_to_check_user),
                type = TopNotificationType.ERROR,
            )
        }
    }

    private fun setupOtpInput() {
        viewBinding.edtOtp.showSoftInputOnFocus = false
        renderOtpStatus(OtpStatus.NORMAL)
    }

    private fun setupInsets() {
        val toolbarPaddingTop = viewBinding.toolbar.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { _, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navigationBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

            viewBinding.toolbar.updatePadding(top = toolbarPaddingTop + statusBar)
            viewBinding.root.updatePadding(bottom = navigationBar)
            insets
        }
        ViewCompat.requestApplyInsets(window.decorView)
    }

    private fun formatCountdown(seconds: Long): String = String.format(
        Locale.US,
        "%02d:%02d",
        seconds / SECONDS_IN_MINUTE,
        seconds % SECONDS_IN_MINUTE,
    )

    companion object {
        const val EXTRA_PHONE_NUMBER = "extra_phone_number"
        const val EXTRA_FULL_NAME = "extra_full_name"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_FLOW = "extra_otp_flow"
        const val FLOW_CREATE_ACCOUNT = "create_account"
        const val FLOW_FORGOT_PASSWORD = "forgot_password"
        private const val SECONDS_IN_MINUTE = 60L
    }
}
