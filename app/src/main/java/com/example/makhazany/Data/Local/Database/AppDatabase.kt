package com.example.makhazany.Data.Local.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.makhazany.Data.Local.Dao.*
import com.example.makhazany.Data.Local.Entity.*

@Database(
    entities = [
        ItemEntity::class,
        CustomerEntity::class,
        StockEntity::class,
        InboundEntity::class,
        OutboundEntity::class,
        OutboundDetailsEntity::class,
        ReturnedEntity::class,
        ReturnedDetailsEntity::class,
        PaymentEntity::class
    ],
    version = 2 // Incremented version because of schema change in InboundEntity
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun customerDao(): CustomerDao
    abstract fun stockDao(): StockDao
    abstract fun inboundDao(): InboundDao
    abstract fun outboundDao(): OutboundDao
    abstract fun outboundDetailsDao(): OutboundDetailsDao
    abstract fun returnedDao(): ReturnedDao
    abstract fun returnedDetailsDao(): ReturnedDetailsDao
    abstract fun paymentDao(): PaymentDao
}
