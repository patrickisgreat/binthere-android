package app.binthere.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "bins",
    foreignKeys = [
        ForeignKey(
            entity = ZoneEntity::class,
            parentColumns = ["id"],
            childColumns = ["zoneId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index("householdId"),
        Index("zoneId"),
    ],
)
data class BinEntity(
    @PrimaryKey
    val id: String,
    val householdId: String,
    val zoneId: String? = null,
    val code: String = "",
    val name: String = "",
    val binDescription: String = "",
    val location: String = "",
    val color: String = "",
    val qrCodeImagePath: String? = null,
    val contentImagePaths: List<String> = emptyList(),
    val createdAt: Instant,
    val updatedAt: Instant,
)
