package com.example.makhazany.Data.Local.Entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.smartstock.Data.Local.E.CustomerEntity

@Entity(
    tableName = "payments",
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
data class PaymentEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val customerId: Int,

    val amount: Double,

    val paymentType: String,

    val paymentDate: Long
)
