package net.orandja.test

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.orandja.either.Empty
import net.orandja.either.Option
import net.orandja.either.Value
import kotlin.test.Test
import kotlin.test.assertEquals

class OptionJson {
    @Serializable
    data class Data(val value: Option<String?> = Empty)

    val codec = Json { encodeDefaults = false }

    @Test
    fun serialization() {
        val empty = codec.encodeToString(Data(Empty))
        val nullV = codec.encodeToString(Data(Value(null)))
        val value = codec.encodeToString(Data(Value("value")))
        assertEquals(empty, "{}")
        assertEquals(nullV, "{\"value\":null}")
        assertEquals(value, "{\"value\":\"value\"}")
    }

    @Test
    fun deserialization() {
        val empty = codec.decodeFromString<Data>("{}")
        val nullV = codec.decodeFromString<Data>("{\"value\":null}")
        val value = codec.decodeFromString<Data>("{\"value\":\"value\"}")
        assertEquals(empty, Data(Empty))
        assertEquals(nullV, Data(Value(null)))
        assertEquals(value, Data(Value("value")))
    }
}