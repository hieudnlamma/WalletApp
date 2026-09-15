package com.lmt.global.base.presenter.main.more.feature.transfer

import androidx.lifecycle.viewModelScope
import com.lmt.global.base.common.IViewModel
import com.lmt.global.base.data.repository.wallet.WalletRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class TransferViewModel(
    walletRepository: WalletRepository,
) : IViewModel<TransferContactsState>() {

    private val userPhoneNumber = appSharedPreferences.walletUserId

    val frequentRecipients = walletRepository
        .frequentRecipients(userPhoneNumber)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList(),
        )

    val allRecipients = walletRepository
        .allRecipients(userPhoneNumber)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList(),
        )

    override fun onState(state: TransferContactsState) = Unit
}

sealed interface TransferContactsState : IViewModel.IState
