package com.lmt.global.base.presenter.payment

import android.os.SystemClock
import com.lmt.global.base.common.IViewModel
import com.lmt.global.base.data.repository.wallet.WalletActionResult
import com.lmt.global.base.data.repository.wallet.WalletRepository
import com.lmt.global.base.model.Transaction
import java.math.BigDecimal
import java.math.RoundingMode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration.Companion.milliseconds

class PaymentViewModel(
    private val walletRepository: WalletRepository,
) : IViewModel<PaymentAction>() {
    private val _effects = MutableSharedFlow<PaymentEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    override fun onState(state: PaymentAction) {
        if (_isProcessing.value) return

        _isProcessing.value = true
        launchWithIO {
            val startedAt = SystemClock.elapsedRealtime()
            val effect = try {
                createEffect(state)
            } catch (throwable: Throwable) {
                if (throwable is CancellationException) throw throwable
                PaymentEffect.Failed
            }

            val elapsed = SystemClock.elapsedRealtime() - startedAt
            delay((MIN_PROCESSING_DURATION_MILLIS - elapsed).coerceAtLeast(0L).milliseconds)
            _isProcessing.value = false
            _effects.emit(effect)
        }
    }

    private suspend fun createEffect(state: PaymentAction): PaymentEffect {
        val amountMinor = state.amount.toMinorUnitsOrNull()
        if (amountMinor == null || amountMinor <= 0L) return PaymentEffect.Failed

        val result = when (state) {
            is PaymentAction.PayBill -> walletRepository.payBill(
                userPhoneNumber = appSharedPreferences.walletUserId,
                billerName = state.transaction.merchantName,
                iconKey = ICON_PAY_BILL,
                amountMinor = amountMinor,
                currencyCode = state.transaction.currencyCode,
                dueText = state.transaction.dueText,
                category = state.transaction.category,
                dueDate = state.transaction.dueDate,
                registrationNumber = state.transaction.registrationNumber,
            )

            is PaymentAction.Transfer -> walletRepository.transfer(
                userPhoneNumber = appSharedPreferences.walletUserId,
                recipientName = state.recipientName,
                recipientPhoneNumber = state.recipientPhoneNumber,
                avatarKey = ICON_TRANSFER,
                amountMinor = amountMinor,
            )
        }
        return result.toEffect(state, amountMinor)
    }

    private fun WalletActionResult.toEffect(
        action: PaymentAction,
        amountMinor: Long,
    ): PaymentEffect = when (this) {
        is WalletActionResult.Success -> PaymentEffect.Succeeded(
            transactionId = transactionId ?: 0L,
            title = when (action) {
                is PaymentAction.PayBill -> action.transaction.merchantName
                is PaymentAction.Transfer -> action.recipientName
            },
            amountMinor = amountMinor,
            type = when (action) {
                is PaymentAction.PayBill -> Transaction.TYPE_PAY_BILL
                is PaymentAction.Transfer -> Transaction.TYPE_TRANSFER
            },
        )

        WalletActionResult.InsufficientBalance -> PaymentEffect.InsufficientBalance
        else -> PaymentEffect.Failed
    }

    private fun BigDecimal.toMinorUnitsOrNull(): Long? = runCatching {
        movePointRight(2).setScale(0, RoundingMode.UNNECESSARY).longValueExact()
    }.getOrNull()

    private companion object {
        const val MIN_PROCESSING_DURATION_MILLIS = 1_500L
        const val ICON_PAY_BILL = "icn_more_paybills"
        const val ICON_TRANSFER = "icn_avatar_default"
    }
}

sealed interface PaymentAction : IViewModel.IState {
    val amount: BigDecimal

    data class PayBill(val transaction: Transaction) : PaymentAction {
        override val amount: BigDecimal = transaction.amount.abs()
    }

    data class Transfer(
        val recipientName: String,
        val recipientPhoneNumber: String,
        override val amount: BigDecimal,
    ) : PaymentAction
}

sealed interface PaymentEffect {
    data class Succeeded(
        val transactionId: Long,
        val title: String,
        val amountMinor: Long,
        val type: String,
    ) : PaymentEffect

    data object InsufficientBalance : PaymentEffect
    data object Failed : PaymentEffect
}
