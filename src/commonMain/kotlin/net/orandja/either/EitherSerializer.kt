package net.orandja.either

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*

/**
 * Serializer for both Left and Right implementation of the Either class.
 *
 * @param L the type of the value on the left side of [Either]
 * @param R the type of the value on the right side of [Either]
 * @property leftDelegate the serializer for the type [L]
 * @property rightDelegate the serializer for the type [R]
 */
class EitherSerializer<L, R>(
    private val leftDelegate: KSerializer<L>,
    private val rightDelegate: KSerializer<R>,
) : KSerializer<Either<L, R>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("net.orandja.either.EitherSerializer") {
        element("left", leftDelegate.descriptor, isOptional = true)
        element("right", rightDelegate.descriptor, isOptional = true)
    }

    override fun deserialize(decoder: Decoder): Either<L, R> {
        var left: Option<L> = None
        var right: Option<R> = None
        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> left = Some(decodeSerializableElement(descriptor, 0, leftDelegate))
                    1 -> right = Some(decodeSerializableElement(descriptor, 1, rightDelegate))
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
        }
        return when {
            left is None && right is None -> throw SerializationException("Either do not contains left or right value")
            left is Some && right is Some -> throw SerializationException("Either contains both left and right values")
            left is Some -> Left(left.value)
            right is Some -> Right(right.value)
            else -> error("unreachable")
        }
    }

    override fun serialize(encoder: Encoder, value: Either<L, R>) {
        encoder.encodeStructure(descriptor) {
            when (value) {
                is Left -> encodeSerializableElement(descriptor, 0, leftDelegate, value.left)
                is Right -> encodeSerializableElement(descriptor, 1, rightDelegate, value.right)
            }
        }
    }
}