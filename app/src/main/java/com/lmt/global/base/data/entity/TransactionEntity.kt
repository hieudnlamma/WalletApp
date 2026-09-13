package com.lmt.global.base.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = RecipientEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipientId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("recipientId"), Index("createdAt")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val type: String,
    val title: String,
    val recipientId: Long? = null,
    val iconKey: String,
    val billerType: String? = null,
    val amountMinor: Long,
    val createdAt: Long
) {
    companion object {
        const val TYPE_TRANSFER = "TRANSFER"
        const val TYPE_PAY_BILL = "PAY_BILL"
    }
}
