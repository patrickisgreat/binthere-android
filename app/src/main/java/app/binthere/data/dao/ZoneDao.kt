package app.binthere.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import app.binthere.data.dao.relations.ZoneWithBins
import app.binthere.data.model.ZoneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ZoneDao {
    @Query("SELECT * FROM zones WHERE householdId = :householdId ORDER BY name")
    fun observeByHousehold(householdId: String): Flow<List<ZoneEntity>>

    @Query("SELECT * FROM zones WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<ZoneEntity?>

    @Transaction
    @Query("SELECT * FROM zones WHERE id = :id LIMIT 1")
    fun observeWithBins(id: String): Flow<ZoneWithBins?>

    @Upsert
    suspend fun upsert(zone: ZoneEntity)

    @Upsert
    suspend fun upsertAll(zones: List<ZoneEntity>)

    @Query("DELETE FROM zones WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM zones WHERE householdId = :householdId")
    suspend fun deleteByHousehold(householdId: String)
}
