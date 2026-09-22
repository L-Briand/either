package net.orandja.test

import net.orandja.either.Either
import net.orandja.either.Left
import net.orandja.either.Option
import net.orandja.either.Right
import net.orandja.either.Some
import net.orandja.either.alsoBoth
import net.orandja.either.alsoLeft
import net.orandja.either.alsoNone
import net.orandja.either.alsoRight
import net.orandja.either.alsoSome
import net.orandja.either.foldBoth
import net.orandja.either.foldLeft
import net.orandja.either.foldNone
import net.orandja.either.foldRight
import net.orandja.either.letAsLeft
import net.orandja.either.letAsRight
import net.orandja.either.letBoth
import net.orandja.either.letLeft
import net.orandja.either.letNoneAsLeft
import net.orandja.either.letNoneAsRight
import net.orandja.either.letRight
import net.orandja.either.letSome
import net.orandja.either.requireLeft
import net.orandja.either.requireNone
import net.orandja.either.requireRight
import net.orandja.either.requireSome
import net.orandja.either.tryLeft
import kotlin.test.Test
import kotlin.test.assertEquals

class Api {
    @Test
    fun testOption() {
        val option: Option<Int> = Some(123)
        option.alsoNone { }
        option.alsoSome { }
        option.alsoBoth({}, {})
        option.letNoneAsLeft { "123" }
        option.letNoneAsRight { "123" }
        option.letSome { it.toString() }
        option.letAsLeft({ it.toString() }, { "123" })
        option.letAsRight({ "123" }, { it.toString() })
        option.foldNone { 123 }
        option.foldBoth({ it.toString() }, { "123" })
        option.requireSome { return }
        option.requireNone { return }
    }

    @Test
    fun testEither() {
        val left: Either<Int, Int> = Left(123)
        left.leftAsOption()
        left.rightAsOption()
        left.invert()
        left.alsoLeft { }
        left.alsoRight { }
        left.alsoBoth({}, {})
        left.letLeft { "" }
        left.letRight { "" }
        left.letBoth({ "" }, { "" })
        left.foldLeft { 0 }
        left.foldRight { 0 }
        left.foldBoth({ "" }, { "" })
        left.requireLeft { return }

        left.tryLeft { Left("A") }.tryLeft { Right("B") }

        val right: Either<Int, Int> = Right(123)
        right.requireRight { return }
    }

    object ERROR

    @Test
    fun transformation() {
        fun String.toInt(): Either<Int, ERROR> = this.toIntOrNull()?.let { Left(it) } ?: Right(ERROR)
        fun Int.inBound(range: IntRange) = if (this in range) Left(this) else Right(ERROR)

        val data: Either<String, ERROR> = Left("123")
        val result: Int = data.tryLeft(String::toInt)
            .tryLeft { it.inBound(0..<100) }
            .requireLeft { return }

        assertEquals(123, result)
    }
}