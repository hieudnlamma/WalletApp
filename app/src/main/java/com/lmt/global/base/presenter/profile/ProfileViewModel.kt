package com.lmt.global.base.presenter.profile

import androidx.lifecycle.viewModelScope
import com.lmt.global.base.common.IViewModel
import com.lmt.global.base.data.repository.user.UserRepository
import com.lmt.global.base.model.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(
    private val userRepository: UserRepository,
) : IViewModel<ProfileState>() {
    private val currentUserPhoneNumber = MutableStateFlow(appSharedPreferences.walletUserId)

    val user = currentUserPhoneNumber
        .flatMapLatest(userRepository::observeByPhoneNumber)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = null,
        )

    private val _effects = MutableSharedFlow<ProfileEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    override fun onState(state: ProfileState) {
        val currentUser = user.value ?: run {
            _effects.tryEmit(ProfileEffect.SaveFailed(state.field))
            return
        }
        val value = state.value.trim()
        if (value.isBlank() && state.field != ProfileField.EMAIL) {
            _effects.tryEmit(ProfileEffect.ValueRequired(state.field))
            return
        }

        val updatedUser = when (state.field) {
            ProfileField.FULL_NAME -> currentUser.copy(fullName = value)
            ProfileField.MOBILE -> currentUser.copy(phoneNumber = value)
            ProfileField.EMAIL -> currentUser.copy(email = value.ifBlank { null })
        }
        saveProfile(currentUser, updatedUser, state.field)
    }

    private fun saveProfile(
        currentUser: User,
        updatedUser: User,
        field: ProfileField,
    ) {
        launchWithIO(
            error = { _effects.tryEmit(ProfileEffect.SaveFailed(field)) },
        ) {
            val saved = userRepository.updateProfile(currentUser.phoneNumber, updatedUser)
            if (!saved) {
                _effects.emit(ProfileEffect.SaveFailed(field))
                return@launchWithIO
            }
            if (updatedUser.phoneNumber != currentUser.phoneNumber) {
                appSharedPreferences.currentUserPhoneNumber = updatedUser.phoneNumber
                currentUserPhoneNumber.value = updatedUser.phoneNumber
            }
            _effects.emit(ProfileEffect.Saved(field))
        }
    }
}

data class ProfileState(
    val field: ProfileField,
    val value: String,
) : IViewModel.IState

enum class ProfileField {
    FULL_NAME,
    MOBILE,
    EMAIL,
}

sealed interface ProfileEffect {
    val field: ProfileField

    data class Saved(override val field: ProfileField) : ProfileEffect
    data class SaveFailed(override val field: ProfileField) : ProfileEffect
    data class ValueRequired(override val field: ProfileField) : ProfileEffect
}
