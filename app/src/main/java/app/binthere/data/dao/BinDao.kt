package app.binthere.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import app.binthere.data.dao.relations.BinWithItems
import app.binthere.data.model.BinEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BinDao {
    @Query("SELECT * FROM bins WHERE householdId = :householdId ORDER BY code")
    fun observeByHousehold(householdId: String): Flow<List<BinEntity>>

    @Query("SELECT * FROM bins WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<BinEntity?>

    @Transaction
    @Query("SELECT * FROM bins WHERE id = :id LIMIT 1")
    fun observeWithItems(id: String): Flow<BinWithItems?>

    @Query("SELECT * FROM bins WHERE code = :code AND householdId = :householdId LIMIT 1")
    suspend fun findByCode(
        householdId: String,
        code: String,
    ): BinEntity?

    @Upsert
    suspend fun upsert(bin: BinEntity)

    @Upsert
    suspend fun upsertAll(bins: List<BinEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bin: BinEntity)

    @Query("DELETE FROM bins WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM bins WHERE householdId = :householdId")
    suspend fun deleteByHousehold(householdId: String)
}
