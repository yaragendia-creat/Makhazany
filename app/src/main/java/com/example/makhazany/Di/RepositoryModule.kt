package com.example.makhazany.Di

import com.example.makhazany.Data.RepositoryImpl.CustomerRepositoryImpl
import com.example.makhazany.Data.RepositoryImpl.InboundRepositoryImpl
import com.example.makhazany.Data.RepositoryImpl.OutboundRepositoryImpl
import com.example.makhazany.Data.RepositoryImpl.StockRepositoryImpl
import com.example.makhazany.Domain.Repository.CustomerRepository
import com.example.makhazany.Domain.Repository.InboundRepository
import com.example.makhazany.Domain.Repository.OutboundRepository
import com.example.makhazany.Domain.Repository.StockRepository
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

    @Binds
    abstract fun bindCustomerRepository(
        impl: CustomerRepositoryImpl
    ): CustomerRepository

    @Binds
    abstract fun bindStockRepository(
        impl: StockRepositoryImpl
    ): StockRepository

    @Binds
    abstract fun bindInboundRepository(
        impl: InboundRepositoryImpl
    ): InboundRepository

}
