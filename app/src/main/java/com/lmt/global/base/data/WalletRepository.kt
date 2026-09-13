package com.lmt.global.base.data

import androidx.room.withTransaction
import com.lmt.global.base.data.entity.RecipientEntity
import com.lmt.global.base.data.entity.TransactionEntity
import com.lmt.global.base.data.entity.WalletEntity
import com.lmt.global.base.data.entity.CardEntity
import java.util.Locale

class WalletRepository(private val database: AppDatabase) {
    private val dao = database.walletDao()

    val balance = dao.observeBalance()
    val recentRecipients = dao.observeRecentRecipients()
    val latestTransactions = dao.observeLatestTransactions(LATEST_TRANSACTION_LIMIT)
    val history = dao.observeHistory()
    val cards = dao.observeCards()

    fun card(cardId: String) = dao.observeCard(cardId)

    suspend fun addBalance(amountMinor: Long): WalletActionResult {
        if (amountMinor <= 0L) return WalletActionResult.InvalidAmount
        return database.withTransaction {
            ensureWallet()
            dao.addBalance(amountMinor)
            WalletActionResult.Success()
        }
    }

    suspend fun addCard(
        cardId: String,
        cardName: String,
        cardNumber: String,
        balanceMinor: Long
    ): WalletActionResult {
        val cleanId = cardId.trim().uppercase(Locale.ROOT)
        val cleanName = cardName.trim().replace(Regex("\\s+"), " ")
        val cleanNumber = cardNumber.filter(Char::isDigit)
        val hasInvalidNumberCharacter = cardNumber.any { !it.isDigit() && !it.isWhitespace() }
        if (
            cleanId.isBlank() || cleanName.isBlank() || hasInvalidNumberCharacter ||
            cleanNumber.length != CARD_NUMBER_LENGTH || balanceMinor < 0L
        ) {
            return WalletActionResult.InvalidCard
        }

        return database.withTransaction {
            ensureWallet()
            val inserted = dao.insertCard(
                CardEntity(
                    id = cleanId,
                    name = cleanName,
                    cardNumber = cleanNumber,
                    balanceMinor = balanceMinor,
                    createdAt = System.currentTimeMillis()
                )
            )
            if (inserted == -1L) return@withTransaction WalletActionResult.DuplicateCard
            if (balanceMinor > 0L) dao.addBalance(balanceMinor)
            WalletActionResult.Success()
        }
    }

    suspend fun transfer(
        recipientName: String,
        avatarKey: String,
        amountMinor: Long
    ): WalletActionResult {
        val cleanName = recipientName.trim().replace(Regex("\\s+"), " ")
        if (cleanName.isBlank()) return WalletActionResult.InvalidRecipient
        if (amountMinor <= 0L) return WalletActionResult.InvalidAmount

        return database.withTransaction {
            ensureWallet()
            if (dao.debitBalance(amountMinor) == 0) {
                return@withTransaction WalletActionResult.InsufficientBalance
            }

            val now = System.currentTimeMillis()
            val normalizedName = cleanName.lowercase(Locale.ROOT)
            val currentRecipient = dao.findRecipient(normalizedName)
            val recipientId = if (currentRecipient == null) {
                dao.insertRecipient(
                    RecipientEntity(
                        name = cleanName,
                        normalizedName = normalizedName,
                        avatarKey = avatarKey,
                        lastTransferAt = now
                    )
                )
            } else {
                dao.updateRecipient(
                    currentRecipient.copy(
                        name = cleanName,
                        avatarKey = avatarKey,
                        lastTransferAt = now
                    )
                )
                currentRecipient.id
            }

            val transactionId = dao.insertTransaction(
                TransactionEntity(
                    type = TransactionEntity.TYPE_TRANSFER,
                    title = cleanName,
                    recipientId = recipientId,
                    iconKey = avatarKey,
                    amountMinor = amountMinor,
                    createdAt = now
                )
            )
            WalletActionResult.Success(transactionId)
        }
    }

    suspend fun payBill(
        billerName: String,
        iconKey: String,
        amountMinor: Long
    ): WalletActionResult {
        if (billerName.isBlank()) return WalletActionResult.InvalidRecipient
        if (amountMinor <= 0L) return WalletActionResult.InvalidAmount

        return database.withTransaction {
            ensureWallet()
            if (dao.debitBalance(amountMinor) == 0) {
                return@withTransaction WalletActionResult.InsufficientBalance
            }
            val transactionId = dao.insertTransaction(
                TransactionEntity(
                    type = TransactionEntity.TYPE_PAY_BILL,
                    title = billerName.trim(),
                    iconKey = iconKey,
                    billerType = iconKey,
                    amountMinor = amountMinor,
                    createdAt = System.currentTimeMillis()
                )
            )
            WalletActionResult.Success(transactionId)
        }
    }

    private suspend fun ensureWallet() {
        dao.insertWallet(WalletEntity())
    }

    companion object {
        private const val LATEST_TRANSACTION_LIMIT = 5
        private const val CARD_NUMBER_LENGTH = 16
    }
}

sealed interface WalletActionResult {
    data class Success(val transactionId: Long? = null) : WalletActionResult
    data object InsufficientBalance : WalletActionResult
    data object InvalidAmount : WalletActionResult
    data object InvalidRecipient : WalletActionResult
    data object InvalidCard : WalletActionResult
    data object DuplicateCard : WalletActionResult
}
