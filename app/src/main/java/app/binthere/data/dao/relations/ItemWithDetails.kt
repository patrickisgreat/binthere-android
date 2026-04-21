package app.binthere.data.dao.relations

import androidx.room.Embedded
import androidx.room.Relation
import app.binthere.data.model.CheckoutRecordEntity
import app.binthere.data.model.CustomAttributeEntity
import app.binthere.data.model.ItemEntity

data class ItemWithDetails(
    @Embedded val item: ItemEntity,
    @Relation(parentColumn = "id", entityColumn = "itemId")
    val customAttributes: List<CustomAttributeEntity>,
    @Relation(parentColumn = "id", entityColumn = "itemId")
    val checkoutHistory: List<CheckoutRecordEntity>,
)
