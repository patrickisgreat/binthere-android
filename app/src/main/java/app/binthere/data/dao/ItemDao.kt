package app.binthere.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import app.binthere.data.dao.relations.ItemWithDetails
import app.binthere.data.model.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE householdId = :householdId ORDER BY name")
    fun observeByHousehold(householdId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE binId = :binId ORDER BY name")
    fun observeByBin(binId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<ItemEntity?>

    @Transaction
    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    fun observeWithDetails(id: String): Flow<ItemWithDetails?>

    @Query(
        "SELECT * FROM items WHERE householdId = :householdId AND " +
            "(name LIKE '%' || :query || '%' OR itemDescription LIKE '%' || :query || '%') " +
            "ORDER BY name",
    )
    fun search(householdId: String, query: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE householdId = :householdId AND isCheckedOut = 1 ORDER BY updatedAt DESC")
    fun observeCheckedOut(householdId: String): Flow<List<ItemEntity>>

    @Upsert
    suspend fun upsert(item: ItemEntity)

    @Upsert
    suspend fun upsertAll(items: List<ItemEntity>)

    @Query("UPDATE items SET binId = :newBinId, updatedAt = :updatedAt WHERE id = :itemId")
    suspend fun moveToBin(itemId: String, newBinId: String?, updatedAt: Long)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM items WHERE householdId = :householdId")
    suspend fun deleteByHousehold(householdId: String)
}
