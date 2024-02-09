package net.orandja.either

import kotlinx.serialization.Serializable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Base implementation. Can either be [Left] or [Right].
 */
@Serializable(EitherSerializer::class)
sealed class Either<out L, out R> {
    /** Underlying value on the [Left] implementation */
    abstract val left: L

    /** Underlying value on the [Right] implementation */
    abstract val right: R

    /**
     * Swap the type of `this`. A [Left] becomes [Right]; A [Right] becomes [Left].
     * @return Inverted object.
     */
    abstract fun invert(): Either<R, L>

    /**
     * @return Depending on `this` kind:
     *  - on [Left]: Option [Some] of the [left] value
     *  - on [Right]: [None]
     */
    abstract fun leftAsOption(): Option<L>

    /**
     * @return Depending on `this` kind:
     *  - on [Left]: [None]
     *  - on [Right]: Option [Some] of the [right] value
     */
    abstract fun rightAsOption(): Option<R>

    /**
     * @return [left] if `this` is [Left] or null
     */
    abstract val leftOrNull: L?

    /**
     * @return [right] if `this` is [Right] or null
     */
    abstract val rightOrNull: R?
}

/**
 * On a [Left] Either, calls the specified function [block] with [Either.left]
 * @return `this`
 */
@OptIn(ExperimentalContracts::class)
inline fun <L, R> Either<L, R>.alsoLeft(
    block: (L) -> Unit,
): Either<L, R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    if (this is Left) block(left)
    return this
}


/**
 * On a [Right] Either, calls the specified function [block] with [Either.right].
 * @return `this`
 */
@OptIn(ExperimentalContracts::class)
inline fun <L, R> Either<L, R>.alsoRight(
    block: (R) -> Unit,
): Either<L, R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    if (this is Right) block(right)
    return this
}

/**
 * Depending on `this` kind:
 * - [Left]: Calls the specified function [onLeft] with [Either.left].
 * - [Right]: Calls the specified function [onRight] with [Either.right].
 * @return `this`.
 */
@OptIn(ExperimentalContracts::class)
inline fun <L, R> Either<L, R>.alsoBoth(
    onLeft: (L) -> Unit,
    onRight: (R) -> Unit,
): Either<L, R> {
    contract {
        callsInPlace(onLeft, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onRight, InvocationKind.AT_MOST_ONCE)
    }
    when (this) {
        is Left -> onLeft(this.left)
        is Right -> onRight(this.right)
    }
    return this
}

/**
 * On a [Left] Either, calls the specified function [block] to transform [Either.left].
 * @return New [Left] object with transformed [Either.left] value.
 */
@OptIn(ExperimentalContracts::class)
inline fun <OldL, NewL, R> Either<OldL, R>.letLeft(
    block: (OldL) -> NewL,
): Either<NewL, R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Left -> Left(block(left))
        is Right -> this
    }
}

/**
 * On a [Right] Either, calls the specified function [block] to transform [Either.right].
 * @return New [Right] object with transformed [Either.right] value.
 */
@OptIn(ExperimentalContracts::class)
inline fun <L, OldR, NewR> Either<L, OldR>.letRight(
    block: (OldR) -> NewR,
): Either<L, NewR> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Left -> this
        is Right -> Right(block(right))
    }
}

/**
 * Depending on `this` kind:
 * - [Left]: Calls the specified function [onLeft] to transform [Either.left].
 * - [Right]: Calls the specified function [onRight] to transform [Either.right].
 * @return New Either object with transformed value.
 */
@OptIn(ExperimentalContracts::class)
inline fun <OldL, OldR, NewL, NewR> Either<OldL, OldR>.letBoth(
    onLeft: (OldL) -> NewL,
    onRight: (OldR) -> NewR,
): Either<NewL, NewR> {
    contract {
        callsInPlace(onLeft, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onRight, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Left -> Left(onLeft(this.left))
        is Right -> Right(onRight(this.right))
    }
}

/**
 * Try to get [Either.left] or calls [block] to return or stops the current execution block.
 * @return [Either.left] value.
 */
@OptIn(ExperimentalContracts::class)
inline fun <L, R> Either<L, R>.requireLeft(
    block: (Right<R>) -> Nothing,
): L {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
        returns() implies (this@requireLeft is Left)
    }
    return when (this) {
        is Left -> left
        is Right -> block(this)
    }
}

/**
 * Try to get [Either.right] or calls [block] to return or stops the current execution block.
 * @return [Either.right] value.
 */
@OptIn(ExperimentalContracts::class)
inline fun <L, R> Either<L, R>.requireRight(
    block: (Left<L>) -> Nothing,
): R {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
        returns() implies (this@requireRight is Right)
    }
    return when (this) {
        is Left -> block(this)
        is Right -> right
    }
}

/**
 * On a [Left] Either, calls the specified function [block] to transform [Either.left] into [Either.right] type.
 * @return Transformed [Either.left] or [Either.right] value.
 */
@OptIn(ExperimentalContracts::class)
inline fun <L, R> Either<L, R>.foldLeft(
    block: (L) -> R,
): R {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Left -> block(this.left)
        is Right -> this.right
    }
}

/**
 * On a [Right] Either, calls the specified function [block] to transform [Either.right] into [Either.left] type.
 * @return Transformed [Either.right] or [Either.left] value.
 */
@OptIn(ExperimentalContracts::class)
inline fun <L, R> Either<L, R>.foldRight(
    block: (R) -> L,
): L {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Left -> this.left
        is Right -> block(this.right)
    }
}

/**
 * Depending on `this` Kind:
 * - [Left]: Calls the specified function [onLeft] to transform [Either.left] into [NewType] object.
 * - [Right]: Calls the specified function [onRight] to transform [Either.right] into [NewType] objects.
 * @return Transformed value
 */
@OptIn(ExperimentalContracts::class)
inline fun <L, R, NewType> Either<L, R>.foldBoth(
    onLeft: (L) -> NewType,
    onRight: (R) -> NewType,
): NewType {
    contract {
        callsInPlace(onLeft, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onRight, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Left -> onLeft(this.left)
        is Right -> onRight(this.right)
    }
}

/**
 * Depending on `this` Kind:
 * - [Left]: Call [block] to transform the current `Either` into another one with the same [R] value.
 * - [Right]: Returns it.
 *
 * @return `Either` with a new Left type.
 */
@OptIn(ExperimentalContracts::class)
inline infix fun <OldL, NewL, R> Either<OldL, R>.tryLeft(
    block: (OldL) -> Either<NewL, R>,
): Either<NewL, R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Left -> block(left)
        is Right -> this
    }
}

/**
 * Depending on `this` Kind:
 * - [Left]: Returns it.
 * - [Right]: Call [block] to transform the current `Either` into another one with the same [L] value.
 *
 * @return `Either` with a new Right type.
 */
@OptIn(ExperimentalContracts::class)
inline infix fun <L, OldR, NewR> Either<L, OldR>.tryRight(
    block: (OldR) -> Either<L, NewR>,
): Either<L, NewR> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Left -> this
        is Right -> block(right)
    }
}