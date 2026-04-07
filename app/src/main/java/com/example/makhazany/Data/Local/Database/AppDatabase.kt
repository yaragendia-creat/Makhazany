import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.makhazany.Data.Local.Dao.CustomerDao
import com.example.makhazany.Data.Local.Dao.InboundDao
import com.example.makhazany.Data.Local.Dao.ItemDao
import com.example.makhazany.Data.Local.Dao.OutboundDao
import com.example.makhazany.Data.Local.Dao.OutboundDetailsDao
import com.example.makhazany.Data.Local.Dao.PaymentDao
import com.example.makhazany.Data.Local.Dao.ReturnedDao
import com.example.makhazany.Data.Local.Dao.ReturnedDetailsDao
import com.example.makhazany.Data.Local.Dao.StockDao
import com.example.makhazany.Data.Local.Entity.OutboundDetailsEntity
import com.example.makhazany.Data.Local.Entity.PaymentEntity
import com.example.makhazany.Data.Local.Entity.ReturnedDetailsEntity
import com.example.makhazany.Data.Local.Entity.ReturnedEntity
import com.example.smartstock.Data.Local.E.CustomerEntity
import com.example.smartstock.Data.Local.Entity.InboundEntity
import com.example.smartstock.Data.Local.Entity.ItemEntity
import com.example.smartstock.Data.Local.Entity.OutboundEntity
import com.example.smartstock.Data.Local.Entity.StockEntity

@Database(
    entities = [

        ItemEntity::class,
        CustomerEntity::class,
        StockEntity::class,
        InboundEntity::class,
        OutboundEntity::class,
        OutboundDetailsEntity::class,
        ReturnedEntity::class,
        ReturnedDetailsEntity::class,
        PaymentEntity::class

    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    abstract fun customerDao(): CustomerDao

    abstract fun stockDao(): StockDao

    abstract fun inboundDao(): InboundDao

    abstract fun outboundDao(): OutboundDao

    abstract fun outboundDetailsDao():
            OutboundDetailsDao

    abstract fun returnedDao(): ReturnedDao

    abstract fun returnedDetailsDao():
            ReturnedDetailsDao

    abstract fun paymentDao(): PaymentDao
}