package app.binthere.data.database.converters

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.time.Instant

private val roomJson = Json { ignoreUnknownKeys = true }
private val stringListSerializer = ListSerializer(String.serializer())
private val stringMapSerializer = MapSerializer(String.serializer(), String.serializer())

/**
 * Room TypeConverters. Arrays and maps persist as JSON strings, Instants as
 * epoch millis (no timezone drift, sortable, cheap).
 */
class RoomConverters {
    @TypeConverter
    fun instantToEpochMillis(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun epochMillisToInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun stringListToJson(value: List<String>?): String? = value?.let { roomJson.encodeToString(stringListSerializer, it) }

    @TypeConverter
    fun jsonToStringList(value: String?): List<String>? = value?.let { roomJson.decodeFromString(stringListSerializer, it) }

    @TypeConverter
    fun stringMapToJson(value: Map<String, String>?): String? = value?.let { roomJson.encodeToString(stringMapSerializer, it) }

    @TypeConverter
    fun jsonToStringMap(value: String?): Map<String, String>? = value?.let { roomJson.decodeFromString(stringMapSerializer, it) }
}
