package com.lmt.global.base.data.mapper

import com.lmt.global.base.data.entity.CardEntity
import com.lmt.global.base.data.entity.RecipientEntity
import com.lmt.global.base.model.Card
import com.lmt.global.base.R
import com.lmt.global.base.data.entity.TransactionEntity
import com.lmt.global.base.model.Recipient
import com.lmt.global.base.model.Transaction
import java.math.BigDecimal
import java.util.Locale
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

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

object RecipientMapper : IMapper<RecipientEntity, Recipient> {
    override fun toModel(entity: RecipientEntity): Recipient {
        return Recipient(
            id = entity.id,
            name = entity.name,
            phoneNumber = entity.phoneNumber,
            avatarRes = when (entity.avatarKey) {
                DEFAULT_AVATAR_KEY -> R.drawable.icn_avatar_default
                else -> R.drawable.icn_avatar_default
            },
        )
    }

    override fun toEntity(model: Recipient): RecipientEntity {
        val cleanName = model.name.trim().replace(Regex("\\s+"), " ")
        return RecipientEntity(
            id = model.id,
            name = cleanName,
            normalizedName = cleanName.lowercase(Locale.ROOT),
            phoneNumber = model.phoneNumber.trim(),
            avatarKey = DEFAULT_AVATAR_KEY,
            lastTransferAt = System.currentTimeMillis(),
        )
    }

    private const val DEFAULT_AVATAR_KEY = "icn_avatar_default"
}

object TransactionMapper : IMapper<TransactionEntity, Transaction> {

    override fun toModel(entity: TransactionEntity): Transaction {
        return Transaction(
            id = entity.id,
            merchantName = entity.title,
            dateTime = formatDateTime(entity.createdAt),
            amount = BigDecimal.valueOf(entity.amountMinor, 2).negate(),
            currencyCode = entity.currencyCode,
            merchantIconRes = when (entity.type) {
                TransactionEntity.TYPE_PAY_BILL -> R.drawable.icn_more_paybills
                else -> R.drawable.icn_avatar_default
            },
            type = entity.type,
            dueText = entity.dueText,
            category = entity.category ?: entity.billerType,
            dueDate = entity.dueDate,
            registrationNumber = entity.registrationNumber
                ?: entity.id.toString().padStart(14, '0'),
            createdAt = entity.createdAt
        )
    }

    override fun toEntity(model: Transaction): TransactionEntity {
        return TransactionEntity(
            id = model.id,
            type = model.type,
            title = model.merchantName,
            recipientId = null,
            iconKey = "",
            billerType = null,
            amountMinor = model.amount
                .negate()
                .movePointRight(2)
                .longValueExact(),
            createdAt = model.createdAt,
            currencyCode = model.currencyCode,
            dueText = model.dueText,
            category = model.category,
            dueDate = model.dueDate,
            registrationNumber = model.registrationNumber,
            userPhoneNumber = ""
        )
    }

    private fun formatDateTime(timestamp: Long): String {
        val zoneId = ZoneId.systemDefault()
        val dateTime = Instant.ofEpochMilli(timestamp).atZone(zoneId)
        val date = dateTime.toLocalDate()
        val today = LocalDate.now(zoneId)

        val prefix = when (date) {
            today -> "Today"
            today.minusDays(1) -> "Yesterday"
            else -> dateTime.format(
                DateTimeFormatter.ofPattern(
                    "MMMM d, yyyy",
                    Locale.ENGLISH
                )
            )
        }

        return "$prefix ${
            dateTime.format(
                DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
            )
        }"
    }
}
