package com.lmt.global.base.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lmt.global.base.data.entity.RecipientEntity
import com.lmt.global.base.data.entity.TransactionEntity
import com.lmt.global.base.data.entity.WalletEntity
import com.lmt.global.base.data.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWallet(wallet: WalletEntity): Long

    @Query("SELECT balanceMinor FROM wallet WHERE id = :walletId")
    fun observeBalance(walletId: Int = WalletEntity.SINGLE_WALLET_ID): Flow<Long?>

    @Query("UPDATE wallet SET balanceMinor = balanceMinor + :amountMinor WHERE id = :walletId")
    suspend fun addBalance(
        amountMinor: Long,
        walletId: Int = WalletEntity.SINGLE_WALLET_ID
    ): Int

    @Query(
        "UPDATE wallet SET balanceMinor = balanceMinor - :amountMinor " +
            "WHERE id = :walletId AND balanceMinor >= :amountMinor"
    )
    suspend fun debitBalance(
        amountMinor: Long,
        walletId: Int = WalletEntity.SINGLE_WALLET_ID
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

    @Query("SELECT * FROM recipients ORDER BY lastTransferAt DESC")
    fun observeRecentRecipients(): Flow<List<RecipientEntity>>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC, id DESC LIMIT :limit")
    fun observeLatestTransactions(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC, id DESC")
    fun observeHistory(): Flow<List<TransactionEntity>>
}
