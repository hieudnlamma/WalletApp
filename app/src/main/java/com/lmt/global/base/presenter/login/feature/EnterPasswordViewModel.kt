package com.lmt.global.base.presenter.login.feature

import com.lmt.global.base.common.IViewModel
import com.lmt.global.base.data.repository.user.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.milliseconds

class EnterPasswordViewModel(
    private val userRepository: UserRepository,
) : IViewModel<EnterPasswordAction>() {

    private val _uiState =
        MutableStateFlow(EnterPasswordUiState())

    val uiState = _uiState.asStateFlow()

    private val _effects =
        MutableSharedFlow<EnterPasswordEffect>(
            extraBufferCapacity = 1,
        )

    val effects = _effects.asSharedFlow()

    override fun onState(state: EnterPasswordAction) {
        when (state) {
            is EnterPasswordAction.Login -> {
                login(
                    phoneNumber = state.phoneNumber,
                    password = state.password,
                )
            }
        }
    }

    private fun login(
        phoneNumber: String,
        password: CharArray,
    ) {
        if (phoneNumber.isBlank()) {
            password.fill('\u0000')
            _effects.tryEmit(EnterPasswordEffect.LoginFailed)
            return
        }

        if (password.isEmpty()) {
            password.fill('\u0000')
            _effects.tryEmit(
                EnterPasswordEffect.PasswordRequired
            )
            return
        }

        if (_uiState.value.isBusy) {
            password.fill('\u0000')
            return
        }

        _uiState.update {
            it.copy(isCheckingPassword = true)
        }

        launchWithIO(
            error = {
                password.fill('\u0000')
                _uiState.value = EnterPasswordUiState()
                _effects.tryEmit(
                    EnterPasswordEffect.LoginFailed
                )
            },
        ) {
            val isCorrect =
                userRepository.verifyPassword(
                    phoneNumber = phoneNumber,
                    password = password,
                )

            if (!isCorrect) {
                _uiState.value = EnterPasswordUiState()
                _effects.emit(
                    EnterPasswordEffect.IncorrectPassword
                )
                return@launchWithIO
            }

            // BCrypt đã kiểm tra đúng.
            // Bắt đầu hiển thị loading trong 1 giây.
            _uiState.update {
                it.copy(
                    isCheckingPassword = false,
                    isLoading = true,
                )
            }

            delay(LOGIN_LOADING_DURATION.milliseconds)

            _uiState.value = EnterPasswordUiState()

            _effects.emit(
                EnterPasswordEffect.LoginSucceeded(
                    phoneNumber = phoneNumber,
                )
            )
        }
    }

    private companion object {
        const val LOGIN_LOADING_DURATION = 1_000L
    }
}

sealed interface EnterPasswordAction : IViewModel.IState {

    data class Login(
        val phoneNumber: String,
        val password: CharArray,
    ) : EnterPasswordAction
}

data class EnterPasswordUiState(
    val isCheckingPassword: Boolean = false,
    val isLoading: Boolean = false,
) {
    val isBusy: Boolean
        get() = isCheckingPassword || isLoading
}

sealed interface EnterPasswordEffect {

    data object PasswordRequired : EnterPasswordEffect

    data object IncorrectPassword : EnterPasswordEffect

    data object LoginFailed : EnterPasswordEffect

    data class LoginSucceeded(
        val phoneNumber: String,
    ) : EnterPasswordEffect
}