package com.example.makhazany.Di

import com.example.makhazany.Data.RepositoryImpl.OutboundRepositoryImpl
import com.example.makhazany.Domain.Repository.OutboundRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindOutboundRepository(
        impl: OutboundRepositoryImpl
    ): OutboundRepository

}