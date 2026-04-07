package com.example.makhazany.Domain.Repository

import com.example.makhazany.Data.Local.Entity.OutboundDetailsEntity
import com.example.makhazany.Data.Local.Relation.OutboundWithDetails
import com.example.smartstock.Data.Local.Entity.OutboundEntity
import kotlinx.coroutines.flow.Flow

interface OutboundRepository {

    suspend fun insertOutbound(
        outbound: OutboundEntity
    ): Long

    suspend fun insertDetails(
        details: List<OutboundDetailsEntity>
    )

    fun getOutboundWithDetails():
            Flow<List<OutboundWithDetails>>

}