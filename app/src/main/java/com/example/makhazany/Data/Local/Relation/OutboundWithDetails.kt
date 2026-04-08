package com.example.makhazany.Data.Local.Relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.makhazany.Data.Local.Entity.OutboundDetailsEntity
import com.example.makhazany.Data.Local.Entity.OutboundEntity

data class OutboundWithDetails(
    @Embedded val outbound: OutboundEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "outboundId"
    )
    val details: List<OutboundDetailsEntity>
)
