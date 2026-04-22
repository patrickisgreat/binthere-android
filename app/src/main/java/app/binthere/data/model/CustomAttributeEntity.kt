package app.binthere.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "custom_attributes",
    foreignKeys = [
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("itemId"),
        Index("householdId"),
    ],
)
data class CustomAttributeEntity(
    @PrimaryKey
    val id: String,
    val itemId: String,
    val householdId: String,
    val name: String = "",
    val type: String = "text",
    val textValue: String = "",
    val numberValue: Double? = null,
    val dateValue: Instant? = null,
    val boolValue: Boolean = false,
    val sortOrder: Int = 0,
)
