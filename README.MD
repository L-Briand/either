# Kotlin Either & Option Multiplatform

`Either` and `Option` implementation in kotlin Multiplatform.

---

`Either` is, like the [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/) class in kotlin, a
discriminated union of two types.
However, it lets you use any Type as the second Type.

`Option` is useful when you need to have a third state to your variable, like in a json object where a field 'presence'
is important. (In most cases, using a nullable variable is fine.)

1. My variable is not here `{}` -> `None`
2. My variable is here but null `{"field":null}` -> `Some(null)`
3. My variable is here `{"field":"value"}` -> `Some("value")`

## Import from maven

### Multiplatform

```kotlin
repositories {
    mavenCentral()
}
val commonMain by getting {
    dependencies {
        implementation("net.orandja.kt:either:2.0.2")
    }
}
```

### Jvm

```kotlin
repositories {
    mavenCentral()
}
dependencies {
    implementation("net.orandja.kt:either:2.0.2")
}
```

## Usage

[dokka documentation here](https://l-briand.github.io/either/either/net.orandja.either/index.html)

### Either

This example is a bit convoluted, but it explains pretty well how to use `Either`.

```kotlin
enum class ErrCode { CONVERT, TOO_LOW, TOO_HIGH, /* ... */ }

// Get information from somewhere
var data: Either<String, ErrCode> = getData()

// Transform left String value to Int
// Other methods can be used to transform a Either class.
val transformed: Either<Int?, ErrCode> = data.letLeft { it.toIntOrNull }

// Transform left Int? to Float without exception.
fun doSomething(value: Either<Int?, ErrCode>): Either<Float, ErrCode> {

    // A 'require' block do not allow to be ended.
    // You need to return or throw an exception inside.
    val percent = value.requireLeft { it: Right<ErrCode> ->
        // Here we return the upper function early, 
        // with the already known error
        return it
    }

    percent ?: return Right(ErrCode.CONVERT)
    if (percent <= 0) return Right(ErrCode.TOO_LOW)
    if (percent >= 100) return Right(ErrCode.TOO_HIGH)
    return Left(percent.toFloat() / 100f)
}

// Show result
when (val result = doSomething(transformed)) {
    is Left -> println("Current progress ${result.left}")
    is Right -> println("Invalid value. Reason ${result.right}")
}
```

The `Either` class is serializable with [kotlinx-serialization](https://github.com/Kotlin/kotlinx.serialization).

It adds a depth level to your serialization with `left` or `right` value.

```
Left("value")  <=> { "left": "value" }
Right(12345)   <=> { "right": 12345 }
```

You will get a deserialization exception if both `left` and `right` are together.

### Option

```kotlin
val data: Option<String?> = getDataFromSomewhere()
val result: Option<Int?> = data.letSome { it?.toIntOrNull() }
when (result) {
    is Some -> println("Success, result: ${result.value ?: "'no value'"}.")
    None -> println("Nothing was found.")
}
```

The `Option` class is also serializable with [kotlinx-serialization](https://github.com/Kotlin/kotlinx.serialization).

Make sure to not encode defaults in your encoder. Like in Json with `{ encodeDefaults = false }`. Then, when defining a
data class, initialize fields to `None`:

```kotlin
@Serializable
data class Data(val value: Option<String?> = None)
```

Doing it this way allows the deserializer to fall back to `None` when the field is not present inside a json object.
If the field is present and null, it deserializes to `Some(null)`.
Given the example above:

```
JSON                 <=> Kotlin
{ }                  <=> Data(None) 
{ "value": null }    <=> Data(Some(null)) 
{ "value": "value" } <=> Data(Some("value"))
```

You can see the [test here](src/commonTest/kotlin/net/orandja/test/OptionJson.kt).

# Api

## Create

```kotlin
var option: Option<String>
option = Some("value")
option = None

var either: Either<String, Int>
either = Left("value")
either = Right(1234)
```

## Side execution

```kotlin
val option: Option<String>
val either: Either<String, Int>

val _: Option<String> = option.alsoNone {}
val _: Option<String> = option.alsoSome { str: String -> }
val _: Option<String> = option.alsoBoth(onNone = { }, onSome = { str -> })

val _: Either<String, Int> = either.alsoLeft { str: String -> }
val _: Either<String, Int> = either.alsoRight { int: Int -> }
val _: Either<String, Int> = either.alsoBoth(onLeft = { str -> }, onRight = { int -> })
```

## Transform

### Either

[//]: # (@formatter:off)
```kotlin
val either: Either<String, Int> = Left("value")

val _: String  = either.left  // Might throw an Exception
val _: Int     = either.right // Might throw an Exception
val _: String? = either.leftOrNull
val _: Int?    = either.rightOrNull

val _: Either<Int, String>  = either.invert() // invert types

val _: Either<Unit, Int>    = either.letLeft { str: String -> Unit }    // Transform left type
val _: Either<String, Unit> = either.letRight { int: Int -> Unit }      // Transform right type
val _: Either<Unit, Unit>   = either.letBoth(onLeft = {}, onRight = {}) // Transform both types

val a: String = either.foldRight { int: Int -> "new" }     // Transform right value to left type
val b: Int    = either.foldLeft { str: String -> 0 }       // Transform left value to right type
val _: Unit   = either.foldBoth(onLeft = {}, onRight = {}) // Transform both left and right value to same type

assertTrue { a == "value" }
assertTrue { b == 0 }
```
[//]: # (@formatter:on)

### Option

[//]: # (@formatter:off)
```kotlin
val option: Option<String> = Some("value")

val _: String  = option.value       // Might throw an Exception
val _: String? = option.valueOrNull

val _: Option<Unit> = option.letSome { str: String -> Unit } // Transform value type

val a: String = option.foldNone { "new" }                  // Create value type on None
val _: Unit   = option.foldBoth(onNone = {}, onSome = {}) // Transform bot
assertTrue { a == "value" }
```
[//]: # (@formatter:on)

### Destructuring

```kotlin
val left = Left("value")
val right = Right(1234)
val value = Some(Any())

val (l: String) = left
val (r: Int) = right
val (v: Any) = value
```

### Either to Option

```kotlin
val either: Either<String, Int> = Left("value")

val a: Option<String> = either.leftAsOption()
val b: Option<Int> = either.rightAsOption()

assert(a is Some<String>)
assert(b is None)
```

### Option to Either

```kotlin
val option: Option<String> = Some("value")

val _: Either<String, Int> = option.letNoneAsRight { 0 }
val _: Either<Int, String> = option.letNoneAsLeft { 0 }

val _: Either<Long, Unit> = option.letAsLeft(onSome = { 0L }, onNone = {})
val _: Either<Unit, Long> = option.letAsRight(onSome = { 0L }, onNone = {})
```

If you know the type of the Option, just create a left or right value with it. `val either = Left(option.value)`

### Chaining

You can use the `try` function to chain calls and have a single error handle at the end.

```kotlin
object Error
fun String.toInt(): Either<Int, ERROR> = this.toIntOrNull()?.let { Left(it) } ?: Right(ERROR)
fun Int.inBound(range: IntRange): Either<Int, ERROR> = if (this in range) Left(this) else Right(ERROR)

val data: Either<String, ERROR> = Left("123")
val result: Int = data.tryLeft(String::toInt)
    .tryLeft { it.inBound(0..<100) }
    .requireLeft { return }

assertEquals(123, result)
```

You can do the same thing with an `Option`

```kotlin
fun String.toInt(): Option<Int> = this.toIntOrNull()?.let { Some(it) } ?: None
fun Int.inBound(range: IntRange): Option<Int> = if (this in range) Some(this) else none

val data: Option<String> = Some("123")
val result: Int = data.trySome(String::toInt)
    .tryLeft { it.inBound(0..<100) }
    .requireSome { return }

assertEquals(123, result)
```

## Requiring values

There are no operators like `either.withLeft { }` or `option.withValue { }` as it implies some sort of error
handling if the type is wrong. Instead, the lib provides the inverted thinking, handles the error case and
continues.

In a `require` block you are requiring to **return** or **throw** an exception.
You cannot let the code go at the end of the block.

You can use destructuring syntax to handle `Either` / `Option` values directly.

#### Either

```kotlin
val either: Either<String, Int> = Left("value")

val _: String = either.requireLeft { value: Right<Int> -> error("Will not fail") }
val _: Int = either.requireRight { value: Left<String> -> error("Will fail") }

fun test() {
    val _: String = either.requireLeft { (value: Int) -> return@test }
    val _: Int = either.requireRight { (value: String) -> return@test }
}
```

#### Option

```kotlin
val option: Option<String> = None

option.requireNone { opt: Option<String> -> error("Will not fail") }
option.requireNone { (value: String) -> error("Will not fail") }

val _: String = option.requireSome { error("Will fail") }
```