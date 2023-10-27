package net.orandja.either

import kotlin.jvm.JvmStatic


/**
 * Left implementation of [Either]
 */
class Left<out L>(override val left: L) : Either<L, Nothing>() {

    /** Exception raised while trying to access [right] value on [Left] class. */
    class AccessRightOnLeftException(value: Any?) :
        IllegalStateException("Cannot get `right` on Left($value)", value as? Throwable)

    companion object {
        /**
         * Static instance of [Left]'s Unit.
         *
         * Can be useful when result is [Left] without the need of a specific type. (Example: `Either<Unit, Failure>`)
         */
        @JvmStatic
        val Unit = Left(kotlin.Unit)
    }

    /**
     * This field cannot be found in [Left].
     * @throws AccessRightOnLeftException
     */
    override val right: Nothing
        get() = throw AccessRightOnLeftException(left)

    /**
     * Allow to destructure [left] value. Useful in lambda.
     *
     * Example:
     * ```kotlin
     * val left: Left = either.requireLeft { (error: Right) -> ... }
     * ```
     */
    operator fun component1(): L = left

    /** @see Either.invert */
    override fun invert(): Either<Nothing, L> = Right(left)

    /** @see Either.leftAsOption */
    override fun leftAsOption(): Option<L> = Value(left)

    /** @see Either.rightAsOption */
    override fun rightAsOption(): Option<Nothing> = Empty

    /** @see Either.leftOrNull */
    override val leftOrNull: L? = left

    /** @see Either.rightOrNull */
    override val rightOrNull: Nothing? = null

    override fun toString(): String = "Left($left)"
}