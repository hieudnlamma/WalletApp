package com.lmt.global.base.presenter.main.more.feature.transfer

import androidx.lifecycle.viewModelScope
import com.lmt.global.base.common.IViewModel
import com.lmt.global.base.data.repository.wallet.WalletRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class TransferViewModel(
    private val walletRepository: WalletRepository,
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
        .allRecipients()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList(),
        )

    override fun onState(state: TransferContactsState) {
        when (state) {
            is TransferContactsState.AddRecipient -> addRecipient(state)
        }
    }

    private fun addRecipient(state: TransferContactsState.AddRecipient) {
        launchWithIO {
            walletRepository.addRecipient(
                name = state.name,
                phoneNumber = state.phoneNumber,
                avatarKey = DEFAULT_AVATAR_KEY,
            )
        }
    }

    private companion object {
        const val DEFAULT_AVATAR_KEY = "icn_avatar_default"
    }
}

sealed interface TransferContactsState : IViewModel.IState {
    data class AddRecipient(
        val name: String,
        val phoneNumber: String,
    ) : TransferContactsState
}
