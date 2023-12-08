package net.orandja.test

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.orandja.either.Either
import net.orandja.either.Left
import net.orandja.either.Right
import kotlin.test.Test
import kotlin.test.assertEquals

class EitherJson {

    private val codec = Json { encodeDefaults = false }

    private val lData = Left("value")
    private val leData: Either<String, Int> = lData
    private val rData = Right("value")
    private val reData: Either<Int, String> = rData

    private val lJson = """{"left":"value"}"""
    private val rJson = """{"right":"value"}"""

    @Test
    fun serialize() {
        assertEquals(lJson, codec.encodeToString(lData))
        assertEquals(lJson, codec.encodeToString(leData))
        assertEquals(rJson, codec.encodeToString(rData))
        assertEquals(rJson, codec.encodeToString(reData))
    }

    @Test
    fun deserialize() {
        assertEquals(lData, codec.decodeFromString(lJson))
        assertEquals(leData, codec.decodeFromString(lJson))
        assertEquals(rData, codec.decodeFromString(rJson))
        assertEquals(reData, codec.decodeFromString(rJson))
    }
}