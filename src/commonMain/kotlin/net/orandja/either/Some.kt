package net.orandja.either

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmStatic

/**
 * Implementation of value [Option]
 */
@Serializable(SomeSerializer::class)
data class Some<out T>(override val value: T) : Option<T>() {
    companion object {

        @JvmStatic
        val Unit = Some(kotlin.Unit)

        @JvmStatic
        val True = Left(true)

        @JvmStatic
        val False = Left(false)
    }

    override fun toString(): String = "Some($value)"

    override val valueOrNull: T? = value
}