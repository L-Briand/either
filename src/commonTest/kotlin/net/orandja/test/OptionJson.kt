package net.orandja.test

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.orandja.either.None
import net.orandja.either.Option
import net.orandja.either.Some
import kotlin.test.Test
import kotlin.test.assertEquals

class OptionJson {
    @Serializable
    data class Data(val value: Option<String?> = None)

    private val codec = Json { encodeDefaults = false }

    private val noneData = Data(None)
    private val nullData = Data(Some(null))
    private val someData = Data(Some("value"))

    private val noneJson = """{}"""
    private val nullJson = """{"value":null}"""
    private val someJson = """{"value":"value"}"""

    @Test
    fun serialization() {
        assertEquals(noneJson, codec.encodeToString(noneData))
        assertEquals(nullJson, codec.encodeToString(nullData))
        assertEquals(someJson, codec.encodeToString(someData))
    }

    @Test
    fun deserialization() {
        assertEquals(noneData, codec.decodeFromString(noneJson))
        assertEquals(nullData, codec.decodeFromString(nullJson))
        assertEquals(someData, codec.decodeFromString(someJson))
    }
}