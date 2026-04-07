package com.example.smartstock.Data.Local.Entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.makhazany.Data.roomDatabase.CustomerEntity

@Entity(
    tableName = "outbound",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("customerId")]
)
data class OutboundEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: String,

    val customerId: Int,

    val invoiceNumber: Int,

    val outboundDate: Long,

    val latitude: Double,

    val longitude: Double,

    val moneyReceived: Double,

    val isSynced: Boolean = false
)