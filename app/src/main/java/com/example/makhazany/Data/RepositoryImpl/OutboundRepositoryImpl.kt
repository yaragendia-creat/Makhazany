package com.example.makhazany.Data.RepositoryImpl

import com.example.makhazany.Data.Local.Dao.OutboundDao
import com.example.makhazany.Data.Local.Dao.OutboundDetailsDao
import com.example.makhazany.Data.Local.Entity.OutboundDetailsEntity
import com.example.makhazany.Data.Local.Relation.OutboundWithDetails
import com.example.makhazany.Domain.Repository.OutboundRepository
import com.example.smartstock.Data.Local.Entity.OutboundEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OutboundRepositoryImpl @Inject constructor(

    private val outboundDao: OutboundDao,
    private val detailsDao: OutboundDetailsDao

) : OutboundRepository {

    override suspend fun insertOutbound(
        outbound: OutboundEntity
    ): Long {

        return outboundDao.insertOutbound(
            outbound
        )
    }

    override suspend fun insertDetails(
        details: List<OutboundDetailsEntity>
    ) {

        details.forEach {

            detailsDao.insertDetails(it)

        }
    }

    override fun getOutboundWithDetails():
            Flow<List<OutboundWithDetails>> {

        return outboundDao
            .getOutboundWithDetails()
    }
}