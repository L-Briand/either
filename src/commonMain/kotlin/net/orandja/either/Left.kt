package net.orandja.either

import kotlinx.serialization.Serializable


/**
 * Left implementation of [Either]
 */
@Serializable(LeftSerializer::class)
value class Left<out L>(override val left: L) : Either<L, Nothing> {

    val value: L get() = left

    /** Exception raised while trying to access [right] value on [Left] class. */
    class AccessRightOnLeftException(value: Any?) :
        IllegalStateException("Cannot get `right` on Left($value)", value as? Throwable)

    companion object {
        val Unit = Left(kotlin.Unit)
        val True = Left(true)
        val False = Left(false)
    }

    /**
     * This field cannot be found in [Left].
     * @throws AccessRightOnLeftException
     */
    override val right: Nothing
        get() = throw AccessRightOnLeftException(left)

    /** @see Either.invert */
    override fun invert(): Either<Nothing, L> = Right(left)

    /** @see Either.leftAsOption */
    override fun leftAsOption(): Option<L> = Some(left)

    /** @see Either.rightAsOption */
    override fun rightAsOption(): Option<Nothing> = None

    /** @see Either.leftOrNull */
    override val leftOrNull: L? get() = left

    /** @see Either.rightOrNull */
    override val rightOrNull: Nothing? get() = null

    override fun toString(): String = "Left($left)"
}