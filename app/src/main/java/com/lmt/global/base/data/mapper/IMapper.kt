package com.lmt.global.base.data.mapper

import com.lmt.global.base.data.entity.CardEntity
import com.lmt.global.base.model.Card

interface IMapper<Entity, Model> {
    fun toModel(entity: Entity): Model
    fun toEntity(model: Model): Entity
}

object CardMapper : IMapper<CardEntity, Card> {
    override fun toModel(entity: CardEntity): Card {
        return Card(
            id = entity.id,
            name = entity.name,
            cardNumber = entity.cardNumber,
            balanceMinor = entity.balanceMinor,
            createdAt = entity.createdAt
        )
    }

    override fun toEntity(model: Card): CardEntity {
       return CardEntity(
           id = model.id,
           name = model.name,
           cardNumber = model.cardNumber,
           balanceMinor = model.balanceMinor,
           createdAt = model.createdAt
       )
    }
}
