package app.binthere.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.binthere.data.dao.BinDao
import app.binthere.data.dao.CheckoutRecordDao
import app.binthere.data.dao.CustomAttributeDao
import app.binthere.data.dao.ItemDao
import app.binthere.data.dao.ZoneDao
import app.binthere.data.database.converters.RoomConverters
import app.binthere.data.model.BinEntity
import app.binthere.data.model.CheckoutRecordEntity
import app.binthere.data.model.CustomAttributeEntity
import app.binthere.data.model.ItemEntity
import app.binthere.data.model.ZoneEntity

@Database(
    entities = [
        ZoneEntity::class,
        BinEntity::class,
        ItemEntity::class,
        CheckoutRecordEntity::class,
        CustomAttributeEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(RoomConverters::class)
abstract class BinthereDatabase : RoomDatabase() {
    abstract fun zoneDao(): ZoneDao

    abstract fun binDao(): BinDao

    abstract fun itemDao(): ItemDao

    abstract fun checkoutRecordDao(): CheckoutRecordDao

    abstract fun customAttributeDao(): CustomAttributeDao

    companion object {
        const val NAME: String = "binthere.db"
    }
}
