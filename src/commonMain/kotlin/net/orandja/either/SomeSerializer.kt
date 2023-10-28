package net.orandja.either

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Default serializer for [SomeSerializer].
 * This serializer does not encapsulate the value into an object with value inside.
 */
class SomeSerializer<T>(private val delegate: KSerializer<T>) : KSerializer<Some<T>> {
    override val descriptor: SerialDescriptor = delegate.descriptor
    override fun deserialize(decoder: Decoder): Some<T> = Some(delegate.deserialize(decoder))

    override fun serialize(encoder: Encoder, value: Some<T>) {
        delegate.serialize(encoder, value.value)
    }
}