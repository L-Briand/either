package net.orandja.either

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmStatic

/**
 * Implementation of value [Option]
 */
@Serializable(SomeSerializer::class)
data class Some<out T>(override val value: T) : Option<T>() {
    companion object {

        /**
         * Static instance of [Some]'s Unit.
         *
         * Can be useful when result is [Some] without the need of a specific type. (Example: `Some<Unit>`)
         */
        @JvmStatic
        val Unit = Some(kotlin.Unit)
    }

    override fun toString(): String = "Some($value)"

    override val valueOrNull: T? = value
}