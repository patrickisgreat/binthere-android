package app.binthere.data.dao.relations

import androidx.room.Embedded
import androidx.room.Relation
import app.binthere.data.model.BinEntity
import app.binthere.data.model.ZoneEntity

data class ZoneWithBins(
    @Embedded val zone: ZoneEntity,
    @Relation(parentColumn = "id", entityColumn = "zoneId")
    val bins: List<BinEntity>,
)
