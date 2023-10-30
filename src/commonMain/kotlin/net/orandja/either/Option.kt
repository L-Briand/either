package net.orandja.either

import kotlinx.serialization.Serializable

/**
 * Base implementation.
 * Can either be [Some] or [None]
 */
@Serializable(OptionSerializer::class)
sealed class Option<out T> {
    /** Underlying value of [Some] implementation */
    abstract val value: T

    /** @return [Some.value] if possible or null */
    abstract val valueOrNull: T?
}

/**
 * On a [Some] Option, calls the specified function [block] with [Some.value]
 * @return `this`
 */
inline fun <T> Option<T>.alsoSome(
    block: (T) -> Unit,
): Option<T> {
    if (this is Some) block(value)
    return this
}

/**
 * On a [None] Option, calls the specified function [block]
 * @return `this`
 */
inline fun <T> Option<T>.alsoNone(
    block: () -> Unit,
): Option<T> {
    if (this is None) block()
    return this
}

/**
 * Depending on `this` kind:
 * - [None]: Calls the specified function [onNone].
 * - [Some]: Calls the specified function [onSome] with [Some.value].
 * @return `this`.
 */
inline fun <T> Option<T>.alsoBoth(
    onNone: () -> Unit,
    onSome: (T) -> Unit,
): Option<T> {
    when (this) {
        None -> onNone()
        is Some -> onSome(this.value)
    }
    return this
}

/**
 * On a [Some] Option, calls the specified function [block] to transform [Some.value].
 * @return New [Some] object with transformed [Option.value] value.
 */
inline fun <T, R> Option<T>.letSome(
    block: (T) -> R,
) = when (this) {
    is Some -> Some(value.let(block))
    None -> None
}

/**
 * Depending on `this` Kind:
 * - [None]: Calls the specified function [onNone] to get [R] type object.
 * - [Some]: Calls the specified function [onSome] to transform [Some.value] into [L] type object.
 * @return [Left]<[L]> or [Right]<[R]> found value.
 */
inline fun <T, L, R> Option<T>.letAsLeft(
    onSome: (T) -> L,
    onNone: () -> R,
): Either<L, R> = when (this) {
    None -> Right(onNone())
    is Some -> Left(onSome(this.value))
}

/**
 * Depending on `this` Kind:
 * - [None]: Calls the specified function [onNone] to get [L] type object.
 * - [Some]: Calls the specified function [onSome] to transform [Some.value] into [R] type object.
 * @return [Left]<[L]> or [Right]<[R]> found value.
 */
inline fun <T, L, R> Option<T>.letAsRight(
    onNone: () -> L,
    onSome: (T) -> R,
): Either<L, R> = when (this) {
    None -> Left(onNone())
    is Some -> Right(onSome(this.value))
}

/**
 * Depending on `this` kind:
 * - [None]: Calls the specified function [block] to get [R] type object.
 * - [Some]<T>: Transform it into [Right]<T> value.
 * @return [Left]<[L]> or [Right]<[R]> found value.
 */
inline fun <L, R> Option<L>.letNoneAsRight(
    block: () -> R,
): Either<L, R> = when (this) {
    is Some -> Left(value)
    None -> Right(block())
}

/**
 * Depending on `this` kind:
 * - [None]: Calls the specified function [block] to get [L] type object.
 * - [Some]<T>: Transform it into [Left]<T> value.
 * @return [Left]<[L]> or [Right]<[R]> found value.
 */
inline fun <R, L> Option<R>.letNoneAsLeft(
    block: () -> L,
): Either<L, R> = when (this) {
    is Some -> Right(value)
    None -> Left(block())
}

/**
 * Try to get [Some.value] or calls [block] to return or stops the current execution block.
 * @return [Some.value] value.
 */
inline fun <T> Option<T>.requireSome(
    block: () -> Nothing,
): T = when (this) {
    None -> block()
    is Some -> value
}

/**
 * If `this` is [Some], calls [block] with `this` to return or stops the current execution block.
 */
inline fun <T> Option<T>.requireNone(
    block: (Some<T>) -> Nothing,
) {
    if (this is Some) block(this)
}

/**
 * Depending on `this` kind:
 * - [None]: Calls the specified function [onNone] to get [O] type object.
 * - [Some]: Calls the specified function [onSome] to transform [Some.value] to [O] type.
 * @return Found [O] type object.
 */
inline fun <T, O> Option<T>.foldBoth(
    onSome: (T) -> O,
    onNone: () -> O,
): O = when (this) {
    None -> onNone()
    is Some -> onSome(this.value)
}

/**
 * Depending on `this` kind:
 * - [None]: Calls the specified function [block] to get [T] type object.
 * - [Some]: Returns [Some.value] object.
 * @return Found [T] type object
 */
inline fun <T> Option<T>.foldNone(
    block: () -> T,
): T = when (this) {
    None -> block()
    is Some -> value
}
