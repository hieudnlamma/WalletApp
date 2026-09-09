package com.lmt.global.base.presenter.create_account.feature

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.lmt.global.base.R
import com.lmt.global.base.common.CommonViewModel
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityInputOptBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class InputOptActivity : IActivity<ActivityInputOptBinding, CommonViewModel>() {
    private var countDownTimer: CountDownTimer? = null
    private var isResendEnabled = false

    override fun provideViewModel() = viewModel<CommonViewModel>()

    override fun provideLayout() = R.layout.activity_input_opt

    override fun initViews(savedInstanceState: Bundle?) {
        setupInsets()
        setupOtpInput()
        startResendCountdown()
    }

    override fun initListeners() {
        viewBinding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewBinding.numberKeyboard.setOnNumberClickListener { number ->
            appendOtpNumber(number)
        }

        viewBinding.numberKeyboard.setOnDeleteClickListener {
            deleteLastOtpNumber()
        }

        viewBinding.llResentCode.setOnClickListener {
            if (isResendEnabled) {
                resendCode()
            }
        }

        viewBinding.btnDone.setOnClickListener {
            verifyOtp()
        }
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
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
        ViewCompat.requestApplyInsets(viewBinding.root)
    }

    private fun setupOtpInput() {
        viewBinding.edtOtp.showSoftInputOnFocus = false
        setOtpNormalState()
    }

    private fun appendOtpNumber(number: String) {
        setOtpNormalState()
        viewBinding.edtOtp.appendDigit(number)
    }

    private fun deleteLastOtpNumber() {
        setOtpNormalState()
        viewBinding.edtOtp.deleteLastDigit()
    }

    private fun startResendCountdown() {
        countDownTimer?.cancel()
        isResendEnabled = false
        updateResendState(secondsRemaining = RESEND_COUNTDOWN_SECONDS)

        countDownTimer = object : CountDownTimer(
            RESEND_COUNTDOWN_SECONDS * MILLIS_IN_SECOND,
            MILLIS_IN_SECOND,
        ) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / MILLIS_IN_SECOND
                updateResendState(secondsRemaining)
            }

            override fun onFinish() {
                isResendEnabled = true
                viewBinding.tvCountdown.text = "00:00"
                viewBinding.tvResentCode.setTextColor(
                    ContextCompat.getColor(
                        this@InputOptActivity,
                        R.color.hues_blue_celtic_blue,
                    ),
                )
            }
        }.start()
    }

    private fun updateResendState(secondsRemaining: Long) {
        viewBinding.tvCountdown.text = formatCountdown(secondsRemaining)
        viewBinding.tvResentCode.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.neutrals_slate_gray,
            ),
        )
    }

    private fun formatCountdown(seconds: Long): String {
        val minutes = seconds / SECONDS_IN_MINUTE
        val remainingSeconds = seconds % SECONDS_IN_MINUTE
        return "%02d:%02d".format(minutes, remainingSeconds)
    }

    private fun resendCode() {
        viewBinding.edtOtp.clearOtp()
        setOtpNormalState()
        startResendCountdown()
        // TODO: call resend OTP API
    }

    private fun verifyOtp() {
        val otp = viewBinding.edtOtp.getOtp()
        if (otp.length < OTP_LENGTH) {
            setOtpErrorState()
            Toast.makeText(
                this,
                getString(R.string.enter_six_digit_code),
                Toast.LENGTH_SHORT,
            ).show()
            return
        }

        checkOtp()
//        viewBinding.edtOtp.setSuccessState()
//        // TODO: validate OTP
    }

    private fun checkOtp() {
        val otp = viewBinding.edtOtp.getOtp()

        if (otp == "123456") {
            setOtpSuccessState()
        } else {
            setOtpErrorState()
        }
    }

    private fun setOtpNormalState() {
        viewBinding.edtOtp.setNormalState()
        setOtpUnderlineColor(R.color.hues_purple_ocean_blue)
    }

    private fun setOtpSuccessState() {
        viewBinding.edtOtp.setSuccessState()
        setOtpUnderlineColor(R.color.hues_green_shamrock)
    }

    private fun setOtpErrorState() {
        viewBinding.edtOtp.setErrorState()
        setOtpUnderlineColor(R.color.hues_red_golden_gate_bridge)
    }

    private fun setOtpUnderlineColor(colorRes: Int) {
        viewBinding.viewOtpUnderline.setBackgroundColor(
            ContextCompat.getColor(
                this,
                colorRes,
            ),
        )
    }

    private companion object {
        const val OTP_LENGTH = 6
        const val RESEND_COUNTDOWN_SECONDS = 60L
        const val SECONDS_IN_MINUTE = 60L
        const val MILLIS_IN_SECOND = 1000L
    }
}
