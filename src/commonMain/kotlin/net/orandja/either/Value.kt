package net.orandja.either

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmStatic

/**
 * Implementation of value [Option]
 */
@Serializable(ValueSerializer::class)
data class Value<out T>(override val value: T) : Option<T>() {
    companion object {

        /**
         * Static instance of [Value]'s Unit.
         *
         * Can be useful when result is [Value] without the need of a specific type. (Example: `Value<Unit>`)
         */
        @JvmStatic
        val Unit = Value(kotlin.Unit)
    }

    override fun toString(): String = "Value($value)"

    override val valueOrNull: T? = value
}