package app.binthere.data.dao.relations

import androidx.room.Embedded
import androidx.room.Relation
import app.binthere.data.model.BinEntity
import app.binthere.data.model.ItemEntity

data class BinWithItems(
    @Embedded val bin: BinEntity,
    @Relation(parentColumn = "id", entityColumn = "binId")
    val items: List<ItemEntity>,
)
