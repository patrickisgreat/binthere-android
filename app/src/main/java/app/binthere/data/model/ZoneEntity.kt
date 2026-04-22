package app.binthere.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Groups bins by physical location (garage, kitchen, closet). Deleting a
 * zone nullifies `Bin.zoneId` (matches Supabase ON DELETE SET NULL).
 *
 * `created_at` exists in Supabase but iOS doesn't sync it; neither do we.
 */
@Entity(
    tableName = "zones",
    indices = [Index("householdId")],
)
data class ZoneEntity(
    @PrimaryKey
    val id: String,
    val householdId: String,
    val name: String = "",
    val locationDescription: String = "",
    val color: String = "",
    val icon: String = "",
    val locations: List<String> = emptyList(),
    val updatedAt: Instant,
)
