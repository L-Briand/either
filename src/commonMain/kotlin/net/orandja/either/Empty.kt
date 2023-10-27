package net.orandja.either

/**
 * Implementation of empty [Option]
 */
data object Empty : Option<Nothing>() {

    /** Exception raised while trying to access [value] on [Empty] class. */
    class AccessValueException : IllegalStateException("Failed to get 'value' on Empty Option.")

    /**
     * It is not possible to get a value while being [Empty].
     * @throws AccessValueException
     */
    override val value: Nothing get() = throw AccessValueException()

    override val valueOrNull: Nothing? = null
}