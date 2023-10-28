package net.orandja.either

/**
 * Implementation of none [Option]
 */
data object None : Option<Nothing>() {

    /** Exception raised while trying to access [value] on [None] class. */
    class AccessSomeException : IllegalStateException("Failed to get 'value' on None Option.")

    /**
     * It is not possible to get a value while being [None].
     * @throws AccessSomeException
     */
    override val value: Nothing get() = throw AccessSomeException()

    override val valueOrNull: Nothing? = null
}