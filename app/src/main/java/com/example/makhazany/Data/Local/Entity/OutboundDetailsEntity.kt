package com.example.makhazany.Data.Local.Entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.smartstock.Data.Local.Entity.ItemEntity
import com.example.smartstock.Data.Local.Entity.OutboundEntity

@Entity(
    tableName = "outbound_details",
    foreignKeys = [

        ForeignKey(
            entity = OutboundEntity::class,
            parentColumns = ["id"],
            childColumns = ["outboundId"],
            onDelete = ForeignKey.CASCADE
        ),

        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("outboundId"),
        Index("itemId")
    ]
)
data class OutboundDetailsEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val outboundId: Int,

    val itemId: Int,

    val amount: Int,

    val price: Double
)