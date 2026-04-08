package com.example.makhazany.Data.Local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.makhazany.Data.Local.Entity.StockEntity

@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(
        stock: StockEntity
    )

    @Query("SELECT * FROM stocks WHERE itemId = :itemId")
    suspend fun getStockByItem(
        itemId: Int
    ): StockEntity?

}
