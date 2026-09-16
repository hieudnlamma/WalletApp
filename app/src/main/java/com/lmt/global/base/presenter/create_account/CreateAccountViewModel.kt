package com.lmt.global.base.presenter.create_account

import com.lmt.global.base.common.IViewModel
import com.lmt.global.base.data.security.PasswordHasher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateAccountViewModel(
    private val passwordHasher: PasswordHasher,
) : IViewModel<CreateAccountAction>() {
    private val _uiState = MutableStateFlow(CreateAccountUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<CreateAccountEffect>()
    val effects = _effects.asSharedFlow()

    override fun onState(state: CreateAccountAction) {
        when (state) {
            is CreateAccountAction.HashPassword ->
                hashPassword(state.password)
        }
    }

    private fun hashPassword(password: String) {
        if (_uiState.value.isHashingPassword) return

        _uiState.value = CreateAccountUiState(
            isHashingPassword = true,
        )

        launchWithDefault(
            error = {
                _uiState.value = CreateAccountUiState()
                _effects.tryEmit(
                    CreateAccountEffect.HashPasswordFailed
                )
            },
        ) {
            val passwordHash = passwordHasher.hash(
                password.toCharArray(),
            )

            _uiState.value = CreateAccountUiState()

            _effects.emit(
                CreateAccountEffect.PasswordHashed(passwordHash)
            )
        }
    }
}
sealed interface CreateAccountAction : IViewModel.IState {

    data class HashPassword(
        val password: String,
    ) : CreateAccountAction
}

data class CreateAccountUiState(
    val isHashingPassword: Boolean = false,
)

sealed interface CreateAccountEffect {

    data class PasswordHashed(
        val passwordHash: String,
    ) : CreateAccountEffect

    data object HashPasswordFailed : CreateAccountEffect
}