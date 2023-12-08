package net.orandja.either

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*

/**
 * Serializer for the Right implementation of the Either class.
 *
 * This serializer is responsible for serializing and deserializing values of type Right<R>.
 *
 * @param R the type parameter of the Right class
 * @property delegate the serializer for values of type R
 */
class RightSerializer<R>(
    private val delegate: KSerializer<R>,
) : KSerializer<Right<R>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("net.orandja.either.RightSerializer") {
        element("right", delegate.descriptor)
    }

    override fun deserialize(decoder: Decoder): Right<R> {
        var right: Option<R> = None
        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> right = Some(decodeSerializableElement(descriptor, 0, delegate))
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
        }
        return when (right) {
            is None -> throw SerializationException("Cannot deserialize left value, left not found.")
            is Some -> Right(right.value)
        }
    }

    override fun serialize(encoder: Encoder, value: Right<R>) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, delegate, value.right)
        }
    }
}