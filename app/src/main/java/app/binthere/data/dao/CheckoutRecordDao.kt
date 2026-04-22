package app.binthere.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import app.binthere.data.model.CheckoutRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckoutRecordDao {
    @Query("SELECT * FROM checkout_records WHERE itemId = :itemId ORDER BY checkedOutAt DESC")
    fun observeByItem(itemId: String): Flow<List<CheckoutRecordEntity>>

    @Query(
        "SELECT * FROM checkout_records WHERE itemId = :itemId AND checkedInAt IS NULL " +
            "ORDER BY checkedOutAt DESC LIMIT 1",
    )
    fun observeActive(itemId: String): Flow<CheckoutRecordEntity?>

    @Query(
        "SELECT * FROM checkout_records WHERE householdId = :householdId AND checkedInAt IS NULL " +
            "ORDER BY checkedOutAt DESC",
    )
    fun observeActiveByHousehold(householdId: String): Flow<List<CheckoutRecordEntity>>

    @Upsert
    suspend fun upsert(record: CheckoutRecordEntity)

    @Upsert
    suspend fun upsertAll(records: List<CheckoutRecordEntity>)

    @Query("DELETE FROM checkout_records WHERE id = :id")
    suspend fun deleteById(id: String)
}
