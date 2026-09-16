package com.lmt.global.base.data.repository.wallet

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.lmt.global.base.data.dao.WalletDao
import com.lmt.global.base.data.entity.CardEntity
import com.lmt.global.base.data.entity.RecipientEntity
import com.lmt.global.base.data.entity.TransactionEntity
import com.lmt.global.base.data.entity.WalletEntity
import com.lmt.global.base.data.mapper.CardMapper
import com.lmt.global.base.data.mapper.TransactionMapper
import com.lmt.global.base.model.Card
import com.lmt.global.base.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

class WalletRepositoryImpl(
    private val walletDao: WalletDao,
) : WalletRepository {

    override val cards: Flow<List<CardEntity>> = walletDao.observeCards()

    override fun recentRecipients(userPhoneNumber: String): Flow<List<RecipientEntity>> =
        walletDao.observeRecentRecipients(userPhoneNumber)

    override fun frequentRecipients(userPhoneNumber: String): Flow<List<RecipientEntity>> =
        walletDao.observeFrequentRecipients(userPhoneNumber)

    override fun allRecipients(userPhoneNumber: String): Flow<List<RecipientEntity>> =
        walletDao.observeAllRecipients(userPhoneNumber)

    override fun balance(userPhoneNumber: String): Flow<Long?> =
        walletDao.observeBalance(userPhoneNumber)

    override fun history(userPhoneNumber: String): Flow<List<Transaction>> =
        walletDao.observeHistory(userPhoneNumber).map { entities ->
            entities.map(TransactionMapper::toModel)
        }

    override fun savedBills(userPhoneNumber: String): Flow<List<Transaction>> =
        walletDao.observeSavedBills(userPhoneNumber).map { entities ->
            entities.map(TransactionMapper::toModel)
        }

    override fun pagedTransactions(userPhoneNumber: String): Flow<PagingData<Transaction>> =
        Pager(
            config = PagingConfig(
                pageSize = TRANSACTION_PAGE_SIZE,
                initialLoadSize = TRANSACTION_PAGE_SIZE,
                prefetchDistance = 2,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { walletDao.pagingTransactions(userPhoneNumber) },
        ).flow.map { pagingData -> pagingData.map(TransactionMapper::toModel) }

    override fun card(cardId: String): Flow<CardEntity?> = walletDao.observeCard(cardId)

    override suspend fun initializeWallet(userPhoneNumber: String) {
        walletDao.insertWallet(WalletEntity(userPhoneNumber = userPhoneNumber))
    }

    override suspend fun addBalance(
        userPhoneNumber: String,
        amountMinor: Long,
    ): WalletActionResult {
        if (amountMinor <= 0L) return WalletActionResult.InvalidAmount
        walletDao.initializeAndAddBalance(
            wallet = WalletEntity(userPhoneNumber),
            amountMinor = amountMinor,
        )
        return WalletActionResult.Success()
    }

    override suspend fun addCard(
        userPhoneNumber: String,
        cardId: String,
        cardName: String,
        cardNumber: String,
        balanceMinor: Long,
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

        val insertedId = walletDao.insertCardAndAddBalance(
            wallet = WalletEntity(userPhoneNumber),
            card = CardEntity(
                id = cleanId,
                name = cleanName,
                cardNumber = cleanNumber,
                balanceMinor = balanceMinor,
                createdAt = System.currentTimeMillis(),
            ),
        )

        return if (insertedId == -1L) {
            WalletActionResult.DuplicateCard
        } else {
            WalletActionResult.Success()
        }
    }

    override suspend fun transfer(
        userPhoneNumber: String,
        recipientName: String,
        recipientPhoneNumber: String,
        avatarKey: String,
        amountMinor: Long,
    ): WalletActionResult {
        val cleanName = recipientName.trim().replace(Regex("\\s+"), " ")
        val cleanPhoneNumber = recipientPhoneNumber.trim()
        if (cleanName.isBlank()) return WalletActionResult.InvalidRecipient
        if (amountMinor <= 0L) return WalletActionResult.InvalidAmount

        val transactionId = walletDao.transferTransaction(
            wallet = WalletEntity(userPhoneNumber),
            recipientName = cleanName,
            recipientPhoneNumber = cleanPhoneNumber,
            normalizedName = cleanName.lowercase(Locale.ROOT),
            avatarKey = avatarKey,
            amountMinor = amountMinor,
            createdAt = System.currentTimeMillis(),
        )

        return if (transactionId == null) {
            WalletActionResult.InsufficientBalance
        } else {
            WalletActionResult.Success(transactionId)
        }
    }

    override suspend fun payBill(
        userPhoneNumber: String,
        billerName: String,
        iconKey: String,
        amountMinor: Long,
        currencyCode: String,
        dueText: String?,
        category: String?,
        dueDate: String?,
        registrationNumber: String?,
    ): WalletActionResult {
        val cleanBillerName = billerName.trim()
        if (billerName.isBlank()) return WalletActionResult.InvalidRecipient
        if (amountMinor <= 0L) return WalletActionResult.InvalidAmount

        val transactionId = walletDao.payBillTransaction (
            wallet = WalletEntity(userPhoneNumber = userPhoneNumber),
            transaction = TransactionEntity(
                    type = TransactionEntity.TYPE_PAY_BILL,
                    title = cleanBillerName,
                    iconKey = iconKey,
                    billerType = category,
                    category = category,
                    amountMinor = amountMinor,
                    createdAt = System.currentTimeMillis(),
                    currencyCode = currencyCode,
                    dueText = dueText,
                    dueDate = dueDate,
                    registrationNumber = registrationNumber,
                    userPhoneNumber = userPhoneNumber,
            )
        )
        return if (transactionId == null) {
            WalletActionResult.InsufficientBalance
        } else {
            WalletActionResult.Success(transactionId)
        }
    }

    override suspend fun getAll(): Flow<List<Card>> {
        return walletDao.observeCards().map { entities -> entities.map(CardMapper::toModel) }
    }

    override suspend fun addCard(card: Card) {
        val entity = CardMapper.toEntity(card)
        walletDao.insertCard(entity)
    }

    private companion object {
        const val TRANSACTION_PAGE_SIZE = 10
        const val CARD_NUMBER_LENGTH = 16
    }
}
