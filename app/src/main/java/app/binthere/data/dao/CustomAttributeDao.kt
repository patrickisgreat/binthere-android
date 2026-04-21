package app.binthere.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import app.binthere.data.model.CustomAttributeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomAttributeDao {
    @Query("SELECT * FROM custom_attributes WHERE itemId = :itemId ORDER BY sortOrder, name")
    fun observeByItem(itemId: String): Flow<List<CustomAttributeEntity>>

    @Upsert
    suspend fun upsert(attribute: CustomAttributeEntity)

    @Upsert
    suspend fun upsertAll(attributes: List<CustomAttributeEntity>)

    @Query("DELETE FROM custom_attributes WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM custom_attributes WHERE itemId = :itemId")
    suspend fun deleteByItem(itemId: String)
}
