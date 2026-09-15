package com.lmt.global.base.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.paging.PagingSource
import com.lmt.global.base.data.entity.RecipientEntity
import com.lmt.global.base.data.entity.TransactionEntity
import com.lmt.global.base.data.entity.WalletEntity
import com.lmt.global.base.data.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWallet(wallet: WalletEntity): Long

    @Query("SELECT balanceMinor FROM wallet WHERE userPhoneNumber = :userPhoneNumber")
    fun observeBalance(userPhoneNumber: String): Flow<Long?>

    @Query(
        "UPDATE wallet SET balanceMinor = balanceMinor + :amountMinor " +
            "WHERE userPhoneNumber = :userPhoneNumber"
    )
    suspend fun addBalance(
        amountMinor: Long,
        userPhoneNumber: String,
    ): Int

    @Query(
        "UPDATE wallet SET balanceMinor = balanceMinor - :amountMinor " +
            "WHERE userPhoneNumber = :userPhoneNumber AND balanceMinor >= :amountMinor"
    )
    suspend fun debitBalance(
        amountMinor: Long,
        userPhoneNumber: String,
    ): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCard(card: CardEntity): Long

    @Query("SELECT * FROM cards ORDER BY createdAt DESC, id ASC")
    fun observeCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id = :cardId LIMIT 1")
    fun observeCard(cardId: String): Flow<CardEntity?>

    @Query("SELECT * FROM recipients WHERE normalizedName = :normalizedName LIMIT 1")
    suspend fun findRecipient(normalizedName: String): RecipientEntity?

    @Insert
    suspend fun insertRecipient(recipient: RecipientEntity): Long

    @Update
    suspend fun updateRecipient(recipient: RecipientEntity)

    @Query(
        "SELECT recipients.* FROM recipients " +
            "INNER JOIN transactions ON transactions.recipientId = recipients.id " +
            "WHERE transactions.userPhoneNumber = :userPhoneNumber " +
            "AND transactions.type = 'TRANSFER' " +
            "GROUP BY recipients.id " +
            "ORDER BY MAX(transactions.createdAt) DESC, MAX(transactions.id) DESC"
    )
    fun observeRecentRecipients(userPhoneNumber: String): Flow<List<RecipientEntity>>

    @Query(
        "SELECT recipients.* FROM recipients " +
            "INNER JOIN transactions ON transactions.recipientId = recipients.id " +
            "WHERE transactions.userPhoneNumber = :userPhoneNumber " +
            "AND transactions.type = 'TRANSFER' " +
            "GROUP BY recipients.id " +
            "ORDER BY COUNT(transactions.id) DESC, MAX(transactions.createdAt) DESC, " +
            "recipients.name COLLATE NOCASE ASC LIMIT 3"
    )
    fun observeFrequentRecipients(userPhoneNumber: String): Flow<List<RecipientEntity>>

    @Query(
        "SELECT recipients.* FROM recipients " +
            "INNER JOIN transactions ON transactions.recipientId = recipients.id " +
            "WHERE transactions.userPhoneNumber = :userPhoneNumber " +
            "AND transactions.type = 'TRANSFER' " +
            "GROUP BY recipients.id " +
            "ORDER BY recipients.name COLLATE NOCASE ASC"
    )
    fun observeAllRecipients(userPhoneNumber: String): Flow<List<RecipientEntity>>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query(
        "SELECT * FROM transactions WHERE userPhoneNumber = :userPhoneNumber " +
            "ORDER BY createdAt DESC, id DESC"
    )
    fun pagingTransactions(userPhoneNumber: String): PagingSource<Int, TransactionEntity>

    @Query(
        "SELECT * FROM transactions WHERE userPhoneNumber = :userPhoneNumber " +
            "ORDER BY createdAt DESC, id DESC"
    )
    fun observeHistory(userPhoneNumber: String): Flow<List<TransactionEntity>>

    @Query(
        "SELECT * FROM transactions WHERE id IN (" +
            "SELECT MAX(id) FROM transactions " +
            "WHERE userPhoneNumber = :userPhoneNumber AND type = 'PAY_BILL' " +
            "GROUP BY COALESCE(NULLIF(registrationNumber, ''), title || '|' || IFNULL(category, ''))" +
            ") ORDER BY createdAt DESC, id DESC"
    )
    fun observeSavedBills(userPhoneNumber: String): Flow<List<TransactionEntity>>

    @Query(
        "UPDATE wallet SET userPhoneNumber = :newPhoneNumber " +
            "WHERE userPhoneNumber = :currentPhoneNumber"
    )
    suspend fun updateWalletOwner(
        currentPhoneNumber: String,
        newPhoneNumber: String,
    ): Int

    @Query(
        "UPDATE transactions SET userPhoneNumber = :newPhoneNumber " +
            "WHERE userPhoneNumber = :currentPhoneNumber"
    )
    suspend fun updateTransactionOwner(
        currentPhoneNumber: String,
        newPhoneNumber: String,
    ): Int
}
