package app.binthere.data.database.converters

import com.google.common.truth.Truth.assertThat
import java.time.Instant
import org.junit.Test

class RoomConvertersTest {
    private val converters = RoomConverters()

    @Test
    fun `instant round trips through epoch millis`() {
        val original = Instant.parse("2026-04-20T12:34:56.789Z")
        val stored = converters.instantToEpochMillis(original)
        val restored = converters.epochMillisToInstant(stored)
        assertThat(restored).isEqualTo(original)
    }

    @Test
    fun `null instant round trips`() {
        assertThat(converters.instantToEpochMillis(null)).isNull()
        assertThat(converters.epochMillisToInstant(null)).isNull()
    }

    @Test
    fun `empty string list round trips`() {
        val stored = converters.stringListToJson(emptyList())
        assertThat(converters.jsonToStringList(stored)).isEqualTo(emptyList<String>())
    }

    @Test
    fun `string list with special characters round trips`() {
        val original = listOf("garage", "it's a \"quoted\" tag", "emoji 📦")
        val stored = converters.stringListToJson(original)
        assertThat(converters.jsonToStringList(stored)).isEqualTo(original)
    }

    @Test
    fun `null string list round trips`() {
        assertThat(converters.stringListToJson(null)).isNull()
        assertThat(converters.jsonToStringList(null)).isNull()
    }

    @Test
    fun `string map round trips preserving entries`() {
        val original = mapOf("color" to "red", "size" to "XL", "note" to "handle with care")
        val stored = converters.stringMapToJson(original)
        assertThat(converters.jsonToStringMap(stored)).isEqualTo(original)
    }

    @Test
    fun `empty string map round trips`() {
        val stored = converters.stringMapToJson(emptyMap())
        assertThat(converters.jsonToStringMap(stored)).isEqualTo(emptyMap<String, String>())
    }
}
