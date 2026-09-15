package com.lmt.global.base.presenter.main.more.feature.paybills

import androidx.lifecycle.viewModelScope
import com.lmt.global.base.common.IViewModel
import com.lmt.global.base.data.repository.wallet.WalletRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class PayBillsViewModel(
    private val walletRepository: WalletRepository,
) : IViewModel<PayBillsState>() {

    val savedBills = flow {
        val userPhoneNumber = appSharedPreferences.walletUserId
        walletRepository.initializeWallet(userPhoneNumber)
        emitAll(walletRepository.savedBills(userPhoneNumber))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList(),
    )

    override fun onState(state: PayBillsState) = Unit
}

sealed interface PayBillsState : IViewModel.IState
