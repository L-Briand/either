package net.orandja.test

import net.orandja.either.*
import kotlin.test.Test
import kotlin.test.assertTrue

@Suppress(
    "UNUSED_VARIABLE", "UNUSED_DESTRUCTURED_PARAMETER_ENTRY", "UNUSED_ANONYMOUS_PARAMETER", "UNUSED_VALUE",
    "RedundantUnitExpression", "unused", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE"
)
class SomeTests {
    @Test
    fun creation() {
        var option: Option<String>
        option = Value("value")
        option = Empty

        var either: Either<String, Int>
        either = Left("value")
        either = Right(1234)
    }

    // @formatter:off
    @Test
    fun eitherTransformations() {
        val either: Either<String, Int> = Left("value")

        val a: Either<Int, String>  = either.invert() // invert types

        val b: String  = either.left        // Might throw an Exception
        // val c: Int     = either.right       // Might throw an Exception
        val d: String? = either.leftOrNull
        val e: Int?    = either.rightOrNull

        val f: Either<Unit, Int>    = either.letLeft { str: String -> Unit }     // Transform left type
        val g: Either<String, Unit> = either.letRight { int: Int -> Unit }       // Transform right type
        val h: Either<Unit, Unit>   = either.letBoth(onLeft = {}, onRight = {})  // Transform both types

        val i: String = either.foldRight { int: Int -> "new" }     // Create left type on Right
        val j: Int    = either.foldLeft { str: String -> 0 }       // Create right type on Left
        val k: Unit   = either.foldBoth(onLeft = {}, onRight = {}) // Transform both left and right value to same type

        assertTrue { i == "value" }
        assertTrue { j == 0 }
    }

    @Test
    fun optionTransformations() {
        val option: Option<String> = Value("value")

        val a: String  = option.value       // Might throw an Exception
        val b: String? = option.valueOrNull

        val c: Option<Unit> = option.letValue { str: String -> Unit } // Transform value type

        val d: String = option.foldEmpty { "new" }              // Create value on Empty
        val e: Unit   = option.foldBoth(onEmpty = {}, onValue = {}) // Transform bot
        assertTrue { d == "value" }
    }

    @Test
    fun eitherToOption() {
        val either: Either<String, Int> = Left("value")

        val a: Option<String> = either.leftAsOption()
        val b: Option<Int>    = either.rightAsOption()
    }

    @Test
    fun optionToEither() {
        val option: Option<String> = Value("value")

        val a: Either<String, Int> = option.letEmptyAsRight { 0 }
        val b: Either<Int, String> = option.letEmptyAsLeft { 0 }

        val c: Either<Unit, Long> = option.letAsLeft(onValue = {}, onEmpty = { 0L })
        val d: Either<Unit, Long> = option.letAsRight(onValue = { 0L }, onEmpty = {})
    }
    // @formatter:on

    fun requireEither() {
        val either: Either<String, Int> = Left("value")

        val a: String = either.requireLeft { value: Right<Int> -> error("") }
        val b: String = either.requireLeft { (value: Int) -> error("") }

        val c: Int = either.requireRight { value: Left<String> -> error("") }
        val d: Int = either.requireRight { (value: String) -> error("") }
    }

    @Test
    fun requireOption() {
        val option: Option<String> = Value("value")

        val a: String = option.requireValue { error("Failed, value is empty") }

        option.requireEmpty { opt: Option<String> -> return }
        option.requireEmpty { (value: String) -> return } // Destructure to access value more easily
    }

}