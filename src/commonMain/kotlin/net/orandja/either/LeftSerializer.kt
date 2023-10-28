package net.orandja.either

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*

class LeftSerializer<L>(
    private val delegate: KSerializer<L>,
) : KSerializer<Left<L>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("net.orandja.either.LeftSerializer") {
        element("left", delegate.descriptor, isOptional = true)
    }

    override fun deserialize(decoder: Decoder): Left<L> {
        var left: Option<L> = None
        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> left = Some(decodeSerializableElement(descriptor, 0, delegate))
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
        }
        return when (left) {
            is None -> throw SerializationException("Cannot deserialize left value, left not found.")
            is Some -> Left(left.value)
        }
    }

    override fun serialize(encoder: Encoder, value: Left<L>) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, delegate, value.left)
        }
    }
}