package com.lmt.global.base.data.repository.wallet

import com.lmt.global.base.model.Card
import kotlinx.coroutines.flow.Flow

interface WalletRepository {

    suspend fun getAll(): Flow<List<Card>>

    suspend fun addCard(card: Card)
}