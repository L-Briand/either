package net.orandja.either

import kotlinx.serialization.Serializable

/**
 * Base implementation.
 * Can either be [Value] or [Empty]
 */
@Serializable(OptionSerializer::class)
sealed class Option<out T> {
    /** Underlying value of [Value] implementation */
    abstract val value: T

    /** @return [Value.value] if possible or null */
    abstract val valueOrNull: T?
}

/**
 * On a [Value] Option, calls the specified function [block] with [Value.value]
 * @return `this`
 */
inline fun <T> Option<T>.alsoValue(
    block: (T) -> Unit,
): Option<T> {
    if (this is Value) block(value)
    return this
}

/**
 * On a [Empty] Option, calls the specified function [block]
 * @return `this`
 */
inline fun <T> Option<T>.alsoEmpty(
    block: () -> Unit,
): Option<T> {
    if (this is Empty) block()
    return this
}

/**
 * Depending on `this` kind:
 * - [Empty]: Calls the specified function [onEmpty].
 * - [Value]: Calls the specified function [onValue] with [Value.value].
 * @return `this`.
 */
inline fun <T> Option<T>.also(
    onEmpty: () -> Unit,
    onValue: (T) -> Unit,
): Option<T> {
    when (this) {
        Empty -> onEmpty()
        is Value -> onValue(this.value)
    }
    return this
}

/**
 * On a [Value] Option, calls the specified function [block] to transform [Value.value].
 * @return New [Value] object with transformed [Option.value] value.
 */
inline fun <T, R> Option<T>.letValue(
    block: (T) -> R,
) = when (this) {
    is Value -> Value(value.let(block))
    Empty -> Empty
}

/**
 * Depending on `this` Kind:
 * - [Empty]: Calls the specified function [onEmpty] to get [R] type object.
 * - [Value]: Calls the specified function [onValue] to transform [Value.value] into [L] type object.
 * @return [Left]<[L]> or [Right]<[R]> found value.
 */
inline fun <T, L, R> Option<T>.letAsLeft(
    onValue: (T) -> L,
    onEmpty: () -> R,
): Either<L, R> = when (this) {
    Empty -> Right(onEmpty())
    is Value -> Left(onValue(this.value))
}

/**
 * Depending on `this` Kind:
 * - [Empty]: Calls the specified function [onEmpty] to get [L] type object.
 * - [Value]: Calls the specified function [onValue] to transform [Value.value] into [R] type object.
 * @return [Left]<[L]> or [Right]<[R]> found value.
 */
inline fun <T, L, R> Option<T>.letAsRight(
    onEmpty: () -> L,
    onValue: (T) -> R,
): Either<L, R> = when (this) {
    Empty -> Left(onEmpty())
    is Value -> Right(onValue(this.value))
}

/**
 * Depending on `this` kind:
 * - [Empty]: Calls the specified function [block] to get [R] type object.
 * - [Value]<T>: Transform it into [Right]<T> value.
 * @return [Left]<[L]> or [Right]<[R]> found value.
 */
inline fun <L, R> Option<L>.letEmptyAsRight(
    block: () -> R,
): Either<L, R> = when (this) {
    is Value -> Left(value)
    Empty -> Right(block())
}

/**
 * Depending on `this` kind:
 * - [Empty]: Calls the specified function [block] to get [L] type object.
 * - [Value]<T>: Transform it into [Left]<T> value.
 * @return [Left]<[L]> or [Right]<[R]> found value.
 */
inline fun <R, L> Option<R>.letEmptyAsLeft(
    block: () -> L,
): Either<L, R> = when (this) {
    is Value -> Right(value)
    Empty -> Left(block())
}


/**
 * Try to get [Value.value] or calls [block] to return or stops the current execution block.
 * @return [Value.value] value.
 */
inline fun <T> Option<T>.requireValue(
    block: () -> Nothing,
): T = when (this) {
    Empty -> block()
    is Value -> value
}

/**
 * If `this` is [Value], calls [block] with `this` to return or stops the current execution block.
 */
inline fun <T> Option<T>.requireEmpty(
    block: (Value<T>) -> Nothing,
) {
    if(this is Value) block(this)
}

/**
 * Depending on `this` kind:
 * - [Empty]: Calls the specified function [onEmpty] to get [O] type object.
 * - [Value]: Calls the specified function [onValue] to transform [Value.value] to [O] type.
 * @return Found [O] type object.
 */
inline fun <T, O> Option<T>.foldBoth(
    onValue: (T) -> O,
    onEmpty: () -> O,
): O = when (this) {
    Empty -> onEmpty()
    is Value -> onValue(this.value)
}

/**
 * Depending on `this` kind:
 * - [Empty]: Calls the specified function [onEmpty] to get [T] type object.
 * - [Value]: Returns [Value.value] object.
 * @return Found [T] type object
 */
inline fun <T> Option<T>.foldEmpty(
    onEmpty: () -> T,
): T = when (this) {
    Empty -> onEmpty()
    is Value -> value
}
