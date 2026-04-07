package com.example.smartstock.Data.Local.Entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inbound",
    foreignKeys = [
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("itemId")]
)
data class InboundEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val itemId: Int,

    val amount: Int,

    val inboundDate: Long,

    val isSynced: Boolean = false
)