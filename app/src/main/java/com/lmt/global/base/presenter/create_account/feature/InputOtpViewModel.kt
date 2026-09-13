package com.lmt.global.base.presenter.create_account.feature

import androidx.lifecycle.viewModelScope
import com.lmt.global.base.common.IViewModel
import com.lmt.global.base.data.repository.user.UserRepository
import com.lmt.global.base.model.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InputOtpViewModel(
    private val userRepository: UserRepository,
) : IViewModel<InputOtpAction>() {

    private val _uiState = MutableStateFlow(InputOtpUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<InputOtpEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    private var countdownJob: Job? = null
    private var hasStartedCountdown = false

    override fun onState(state: InputOtpAction) {
        when (state) {
            InputOtpAction.StartCountdown -> startCountdownIfNeeded()
            InputOtpAction.ResendCode -> resendCode()
            InputOtpAction.ResetOtpState -> resetOtpState()
            is InputOtpAction.VerifyOtp -> verifyOtp(state)
        }
    }

    private fun startCountdownIfNeeded() {
        if (hasStartedCountdown) return
        hasStartedCountdown = true
        startCountdown()
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    secondsRemaining = RESEND_COUNTDOWN_SECONDS,
                    isResendEnabled = false,
                )
            }

            for (seconds in RESEND_COUNTDOWN_SECONDS downTo 1L) {
                _uiState.update {
                    it.copy(
                        secondsRemaining = seconds,
                        isResendEnabled = false,
                    )
                }
                delay(MILLIS_IN_SECOND)
            }

            _uiState.update {
                it.copy(
                    secondsRemaining = 0L,
                    isResendEnabled = true,
                )
            }
        }
    }

    private fun resendCode() {
        if (!_uiState.value.isResendEnabled) return

        _uiState.update { it.copy(otpStatus = OtpStatus.NORMAL) }
        _effects.tryEmit(InputOtpEffect.ClearOtp)
        startCountdown()

        // TODO: Call resend OTP API.
    }

    private fun resetOtpState() {
        if (_uiState.value.otpStatus == OtpStatus.NORMAL) return
        _uiState.update { it.copy(otpStatus = OtpStatus.NORMAL) }
    }

    private fun verifyOtp(action: InputOtpAction.VerifyOtp) {
        if (action.otp.length != OTP_LENGTH) {
            _uiState.update { it.copy(otpStatus = OtpStatus.ERROR) }
            _effects.tryEmit(InputOtpEffect.ShowInvalidOtpLength)
            return
        }

        if (action.otp != TEST_OTP) {
            _uiState.update { it.copy(otpStatus = OtpStatus.ERROR) }
            return
        }

        _uiState.update { it.copy(otpStatus = OtpStatus.SUCCESS) }
        if (action.isForgotPassword) {
            _effects.tryEmit(InputOtpEffect.PasswordResetVerified)
        } else {
            saveVerifiedUser(action)
        }
    }

    private fun saveVerifiedUser(action: InputOtpAction.VerifyOtp) {
        val phoneNumber = action.phoneNumber
        if (phoneNumber == null) {
            _effects.tryEmit(InputOtpEffect.SaveUserFailed)
            return
        }
        if (_uiState.value.isSavingUser) return
        _uiState.update { it.copy(isSavingUser = true) }

        launchWithIO(
            error = {
                _uiState.update { state -> state.copy(isSavingUser = false) }
                _effects.tryEmit(InputOtpEffect.SaveUserFailed)
            },
        ) {
            userRepository.add(
                User(
                    phoneNumber = phoneNumber,
                    fullName = action.fullName,
                    email = action.email,
                )
            )
            _uiState.update { it.copy(isSavingUser = false) }
            _effects.emit(InputOtpEffect.AccountCreated)
        }
    }

    private companion object {
        const val TEST_OTP = "123456"
        const val OTP_LENGTH = 6
        const val RESEND_COUNTDOWN_SECONDS = 60L
        const val MILLIS_IN_SECOND = 1_000L
    }
}

sealed interface InputOtpAction : IViewModel.IState {
    data object StartCountdown : InputOtpAction
    data object ResendCode : InputOtpAction
    data object ResetOtpState : InputOtpAction

    data class VerifyOtp(
        val otp: String,
        val phoneNumber: String?,
        val fullName: String?,
        val email: String?,
        val isForgotPassword: Boolean,
    ) : InputOtpAction
}

data class InputOtpUiState(
    val secondsRemaining: Long = 60L,
    val isResendEnabled: Boolean = false,
    val otpStatus: OtpStatus = OtpStatus.NORMAL,
    val isSavingUser: Boolean = false,
)

enum class OtpStatus {
    NORMAL,
    SUCCESS,
    ERROR,
}

sealed interface InputOtpEffect {
    data object ClearOtp : InputOtpEffect
    data object ShowInvalidOtpLength : InputOtpEffect
    data object AccountCreated : InputOtpEffect
    data object PasswordResetVerified : InputOtpEffect
    data object SaveUserFailed : InputOtpEffect
}
