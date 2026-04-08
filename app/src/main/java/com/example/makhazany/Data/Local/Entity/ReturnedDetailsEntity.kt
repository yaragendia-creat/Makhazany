package com.example.makhazany.Data.Local.Entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "returned_details",
    foreignKeys = [
        ForeignKey(
            entity = ReturnedEntity::class,
            parentColumns = ["id"],
            childColumns = ["returnedId"],
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
        Index("returnedId"),
        Index("itemId")
    ]
)
data class ReturnedDetailsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val returnedId: Int,
    val itemId: Int,
    val amount: Int,
    val price: Double
)
