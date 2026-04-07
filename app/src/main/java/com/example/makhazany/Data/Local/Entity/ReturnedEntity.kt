package com.example.makhazany.Data.Local.Entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.makhazany.Data.roomDatabase.CustomerEntity

@Entity(
    tableName = "returned",
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
data class ReturnedEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val customerId: Int,

    val returnedDate: Long,

    val userId: String,

    val latitude: Double,

    val longitude: Double
)
