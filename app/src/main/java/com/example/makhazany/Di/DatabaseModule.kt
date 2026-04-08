package com.example.makhazany.Di

import android.content.Context
import androidx.room.Room
import com.example.makhazany.Data.Local.Database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "makhazany_db"
        ).build()
    }

    @Provides
    fun provideItemDao(db: AppDatabase) = db.itemDao()

    @Provides
    fun provideCustomerDao(db: AppDatabase) = db.customerDao()

    @Provides
    fun provideStockDao(db: AppDatabase) = db.stockDao()

    @Provides
    fun provideInboundDao(db: AppDatabase) = db.inboundDao()

    @Provides
    fun provideOutboundDao(db: AppDatabase) = db.outboundDao()

    @Provides
    fun provideOutboundDetailsDao(db: AppDatabase) = db.outboundDetailsDao()

    @Provides
    fun provideReturnedDao(db: AppDatabase) = db.returnedDao()

    @Provides
    fun provideReturnedDetailsDao(db: AppDatabase) = db.returnedDetailsDao()

    @Provides
    fun providePaymentDao(db: AppDatabase) = db.paymentDao()
}
