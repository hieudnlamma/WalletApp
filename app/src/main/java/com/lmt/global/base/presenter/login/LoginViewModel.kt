package com.lmt.global.base.presenter.login

import com.lmt.global.base.common.IViewModel
import com.lmt.global.base.data.repository.user.UserRepository
import com.lmt.global.base.model.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class LoginViewModel(
    private val userRepository: UserRepository,
) : IViewModel<LoginResult>() {
    private val _results = MutableSharedFlow<LoginResult>(extraBufferCapacity = 1)
    val results = _results.asSharedFlow()

    fun checkRegistration(phoneNumber: String) {
        checkRegistration { userRepository.findByPhoneNumber(phoneNumber) }
    }

    private fun checkRegistration(findUser: suspend () -> User?) {
        launchWithIO(
            error = { _results.tryEmit(LoginResult.Error(it)) },
        ) {
            val user = findUser()
            _results.emit(
                if (user == null) {
                    LoginResult.NotRegistered
                } else {
                    LoginResult.Registered(user)
                }
            )
        }
    }

    override fun onState(state: LoginResult) = Unit
}

sealed interface LoginResult : IViewModel.IState {
    data class Registered(val user: User) : LoginResult
    data object NotRegistered : LoginResult
    data class Error(val throwable: Throwable) : LoginResult
}
