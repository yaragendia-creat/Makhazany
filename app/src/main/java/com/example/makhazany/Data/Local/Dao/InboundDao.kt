package com.example.makhazany.Data.Local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.smartstock.Data.Local.Entity.InboundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InboundDao {

    @Insert
    suspend fun insertInbound(
        inbound: InboundEntity
    )

    @Query("SELECT * FROM inbound")
    fun getInbound():
            Flow<List<InboundEntity>>

}