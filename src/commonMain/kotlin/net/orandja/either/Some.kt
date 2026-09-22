package net.orandja.either

import kotlinx.serialization.Serializable

/**
 * Implementation of value [Option]
 */
@Serializable(SomeSerializer::class)
value class Some<out T>(override val value: T) : Option<T> {
    companion object {
        val Unit = Some(kotlin.Unit)
        val True = Left(true)
        val False = Left(false)
    }

    override fun toString(): String = "Some($value)"

    override val valueOrNull: T? get() = value
}