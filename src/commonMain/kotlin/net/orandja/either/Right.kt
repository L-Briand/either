package net.orandja.either

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmStatic

/**
 * Right implementation of [Either]
 */
@Serializable(RightSerializer::class)
data class Right<out R>(override val right: R) : Either<Nothing, R>() {

    /** Exception raised while trying to access [right] value on [Left] class. */
    class AccessLeftOnRightException(value: Any?) :
        IllegalStateException("Cannot get `left` on Right($value)", value as? Throwable)

    companion object {
        /**
         * Static instance of [Right]'s Unit.
         */
        @JvmStatic
        val Unit = Right(kotlin.Unit)
    }

    /**
     * This field cannot be found in [Left].
     * @throws AccessLeftOnRightException
     */
    override val left: Nothing
        get() = throw AccessLeftOnRightException(right)

    /** @see Either.invert */
    override fun invert(): Either<R, Nothing> = Left(right)

    /** @see Either.leftAsOption */
    override fun leftAsOption(): Option<Nothing> = None

    /** @see Either.rightAsOption */
    override fun rightAsOption(): Option<R> = Some(right)

    /** @see Either.leftOrNull */
    override val leftOrNull: Nothing? = null

    /** @see Either.rightOrNull */
    override val rightOrNull: R? = right

    override fun toString(): String = "Right($right)"
}
