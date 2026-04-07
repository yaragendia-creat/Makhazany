package com.example.smartstock.Data.Local.E

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer")
data class CustomerEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: String,

    val customerName: String,

    val customerNum: Int,

    val customerDebt: Double,

    val isSynced: Boolean
)