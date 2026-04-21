package app.binthere.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "checkout_records",
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
data class CheckoutRecordEntity(
    @PrimaryKey
    val id: String,
    val itemId: String,
    val householdId: String,
    val checkedOutBy: String = "",
    val checkedOutTo: String = "",
    val checkedOutAt: Instant,
    val checkedInAt: Instant? = null,
    val expectedReturnDate: Instant? = null,
    val notes: String = "",
)
