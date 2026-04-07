import dagger.Provides

@Provides
fun provideCustomerDao(
    db: AppDatabase
) = db.customerDao()

@Provides
fun provideStockDao(
    db: AppDatabase
) = db.stockDao()

@Provides
fun provideInboundDao(
    db: AppDatabase
) = db.inboundDao()

@Provides
fun provideOutboundDetailsDao(
    db: AppDatabase
) = db.outboundDetailsDao()

@Provides
fun provideReturnedDetailsDao(
    db: AppDatabase
) = db.returnedDetailsDao()

@Provides
fun providePaymentDao(
    db: AppDatabase
) = db.paymentDao()