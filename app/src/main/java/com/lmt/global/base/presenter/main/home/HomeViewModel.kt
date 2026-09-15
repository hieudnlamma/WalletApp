package com.lmt.global.base.presenter.main.home

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.lmt.global.base.common.IViewModel
import com.lmt.global.base.data.entity.WalletEntity
import com.lmt.global.base.data.repository.user.UserRepository
import com.lmt.global.base.data.repository.wallet.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val walletRepository: WalletRepository,
    private val userRepository: UserRepository,
) : IViewModel<HomeState>() {
    private val currentUserPhoneNumber = MutableStateFlow(appSharedPreferences.walletUserId)

    val balance = currentUserPhoneNumber.flatMapLatest { userPhoneNumber ->
        flow {
            walletRepository.initializeWallet(userPhoneNumber)
            emitAll(walletRepository.balance(userPhoneNumber))
        }
    }.map { it ?: WalletEntity.INITIAL_BALANCE_MINOR }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = WalletEntity.INITIAL_BALANCE_MINOR,
        )

    val transactions = currentUserPhoneNumber
        .flatMapLatest(walletRepository::pagedTransactions)
        .cachedIn(viewModelScope)

    val user = currentUserPhoneNumber
        .flatMapLatest(userRepository::observeByPhoneNumber)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = null,
        )

    val recentRecipients = currentUserPhoneNumber
        .flatMapLatest(walletRepository::recentRecipients)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList(),
        )

    fun refreshCurrentUser() {
        currentUserPhoneNumber.value = appSharedPreferences.walletUserId
    }

    override fun onState(state: HomeState) = Unit

}

sealed class HomeState : IViewModel.IState
