package app.binthere.data.database.di

import android.content.Context
import androidx.room.Room
import app.binthere.data.dao.BinDao
import app.binthere.data.dao.CheckoutRecordDao
import app.binthere.data.dao.CustomAttributeDao
import app.binthere.data.dao.ItemDao
import app.binthere.data.dao.ZoneDao
import app.binthere.data.database.BinthereDatabase
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
        @ApplicationContext context: Context,
    ): BinthereDatabase =
        Room.databaseBuilder(context, BinthereDatabase::class.java, BinthereDatabase.NAME)
            .build()

    @Provides
    fun provideZoneDao(db: BinthereDatabase): ZoneDao = db.zoneDao()

    @Provides
    fun provideBinDao(db: BinthereDatabase): BinDao = db.binDao()

    @Provides
    fun provideItemDao(db: BinthereDatabase): ItemDao = db.itemDao()

    @Provides
    fun provideCheckoutRecordDao(db: BinthereDatabase): CheckoutRecordDao = db.checkoutRecordDao()

    @Provides
    fun provideCustomAttributeDao(db: BinthereDatabase): CustomAttributeDao = db.customAttributeDao()
}
