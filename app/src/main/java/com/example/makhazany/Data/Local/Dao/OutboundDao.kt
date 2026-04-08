package com.example.makhazany.Data.Local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.makhazany.Data.Local.Relation.OutboundWithDetails
import com.example.makhazany.Data.Local.Entity.OutboundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OutboundDao {

    @Insert
    suspend fun insertOutbound(
        outbound: OutboundEntity
    ): Long

    @Transaction
    @Query("SELECT * FROM outbound")
    fun getOutboundWithDetails(): Flow<List<OutboundWithDetails>>
}
