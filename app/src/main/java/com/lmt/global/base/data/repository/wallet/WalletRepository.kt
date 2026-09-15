package com.lmt.global.base.data.repository.wallet

import androidx.paging.PagingData
import com.lmt.global.base.data.entity.CardEntity
import com.lmt.global.base.data.entity.RecipientEntity
import com.lmt.global.base.data.entity.TransactionEntity
import com.lmt.global.base.model.Card
import com.lmt.global.base.model.Transaction
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    val cards: Flow<List<CardEntity>>

    fun recentRecipients(userPhoneNumber: String): Flow<List<RecipientEntity>>

    fun frequentRecipients(userPhoneNumber: String): Flow<List<RecipientEntity>>

    fun allRecipients(userPhoneNumber: String): Flow<List<RecipientEntity>>

    fun balance(userPhoneNumber: String): Flow<Long?>

    fun history(userPhoneNumber: String): Flow<List<Transaction>>

    fun savedBills(userPhoneNumber: String): Flow<List<Transaction>>

    fun pagedTransactions(userPhoneNumber: String): Flow<PagingData<Transaction>>

    fun card(cardId: String): Flow<CardEntity?>

    suspend fun initializeWallet(userPhoneNumber: String)

    suspend fun addBalance(
        userPhoneNumber: String,
        amountMinor: Long,
    ): WalletActionResult

    suspend fun addCard(
        userPhoneNumber: String,
        cardId: String,
        cardName: String,
        cardNumber: String,
        balanceMinor: Long,
    ): WalletActionResult

    suspend fun transfer(
        userPhoneNumber: String,
        recipientName: String,
        recipientPhoneNumber: String,
        avatarKey: String,
        amountMinor: Long,
    ): WalletActionResult

    suspend fun payBill(
        userPhoneNumber: String,
        billerName: String,
        iconKey: String,
        amountMinor: Long,
        currencyCode: String = TransactionEntity.DEFAULT_CURRENCY_CODE,
        dueText: String? = null,
        category: String? = null,
        dueDate: String? = null,
        registrationNumber: String? = null,
    ): WalletActionResult

    suspend fun getAll(): Flow<List<Card>>

    suspend fun addCard(card: Card)
}

sealed interface WalletActionResult {
    data class Success(val transactionId: Long? = null) : WalletActionResult
    data object InsufficientBalance : WalletActionResult
    data object InvalidAmount : WalletActionResult
    data object InvalidRecipient : WalletActionResult
    data object InvalidCard : WalletActionResult
    data object DuplicateCard : WalletActionResult
}
