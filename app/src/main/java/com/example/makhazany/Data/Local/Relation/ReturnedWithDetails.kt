package com.example.makhazany.Data.Local.Relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.makhazany.Data.Local.Entity.ReturnedDetailsEntity
import com.example.makhazany.Data.Local.Entity.ReturnedEntity

data class ReturnedWithDetails(

    @Embedded
    val returned: ReturnedEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "returnedId"
    )
    val details: List<ReturnedDetailsEntity>
)
