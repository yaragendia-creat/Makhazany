package com.example.makhazany.Data.Local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.makhazany.Data.Local.Entity.ReturnedEntity
import com.example.makhazany.Data.Local.Relation.ReturnedWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ReturnedDao {

    @Insert
    suspend fun insertReturned(
        returned: ReturnedEntity
    ): Long

    @Transaction
    @Query("SELECT * FROM returned")
    fun getReturnedWithDetails():
            Flow<List<ReturnedWithDetails>>
}