package com.lmt.global.base.data.repository.wallet

import com.lmt.global.base.data.dao.WalletDao
import com.lmt.global.base.data.mapper.CardMapper
import com.lmt.global.base.model.Card
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WalletRepositoryImpl(
    private val walletDao: WalletDao
) : WalletRepository {

    override suspend fun getAll(): Flow<List<Card>> {
        val flowEntities = walletDao.observeCards()
        val flowModel = flowEntities.map { it.map { CardMapper.toModel(it) } }
        return flowModel
    }

    override suspend fun addCard(card: Card) {
        val entity = CardMapper.toEntity(card)
        walletDao.insertCard(entity)
    }
}
