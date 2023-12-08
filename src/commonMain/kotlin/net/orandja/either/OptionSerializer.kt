package net.orandja.either

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * This serializer is used to encode/decode options with field presence inside JSON.
 *
 * Make sure to initialize Json with `{ encodeDefaults = false }`
 * When defining a data class, initialize fields to None.
 *
 * ```kotlin
 * @Serializable
 * data class Data(val myOpt: Option<String?> = None)
 * ```
 *
 * Doing it this way allows the deserializer fallback to none when the field is not present inside a json object.
 * If the field is present and null, it deserializes to Some(null)
 */
class OptionSerializer<T>(private val delegate: KSerializer<T>) : KSerializer<Option<T>> {
    override val descriptor: SerialDescriptor = delegate.descriptor
    override fun deserialize(decoder: Decoder): Option<T> =
        Some(delegate.deserialize(decoder))

    override fun serialize(encoder: Encoder, value: Option<T>) {
        if (value is Some) delegate.serialize(encoder, value.value)
    }
}

