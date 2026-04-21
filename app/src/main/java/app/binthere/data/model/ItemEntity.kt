package app.binthere.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * `customFields` is local-only — iOS persists it client-side and does not
 * sync it to Supabase. We honor the same contract: keep it in the entity
 * but exclude it from the DTO/mapper when syncing.
 */
@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = BinEntity::class,
            parentColumns = ["id"],
            childColumns = ["binId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("householdId"),
        Index("binId"),
    ],
)
data class ItemEntity(
    @PrimaryKey
    val id: String,
    val householdId: String,
    val binId: String? = null,
    val name: String = "",
    val itemDescription: String = "",
    val imagePaths: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val customFields: Map<String, String> = emptyMap(),
    val color: String = "",
    val notes: String = "",
    val value: Double? = null,
    val valueSource: String = "",
    val valueUpdatedAt: Instant? = null,
    val isCheckedOut: Boolean = false,
    val createdBy: String = "",
    val checkoutPermission: String = "anyone",
    val allowedCheckoutUsers: List<String> = emptyList(),
    val maxCheckoutDays: Int? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
)
