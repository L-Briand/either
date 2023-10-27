package net.orandja.either

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Default serializer for [ValueSerializer].
 * This serializer does not encapsulate the value into an object with value inside.
 */
class ValueSerializer<T>(private val delegate: KSerializer<T>) : KSerializer<Value<T>> {
    override val descriptor: SerialDescriptor = delegate.descriptor
    override fun deserialize(decoder: Decoder): Value<T> = Value(delegate.deserialize(decoder))

    override fun serialize(encoder: Encoder, value: Value<T>) {
        delegate.serialize(encoder, value.value)
    }
}