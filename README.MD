# Kotlin Either & Option Multiplatform

`Either` and `Option` implementation in kotlin Multiplatform.

## Import from maven


### Multiplatform

```kotlin
repositories {
    mavenCentral()
}
val commonMain by getting {
    dependencies {
        implementation("net.orandja.kt:either:1.0.0")
    }
}
```

### Jvm

```kotlin
repositories {
    mavenCentral()
}
dependencies {
    implementation("net.orandja.kt:either:1.0.0")
}
```

## Usage

### Either

This example is a bit convoluted, but it explains pretty well how to use `Either`.

```kotlin
enum class ErrCode { CONVERT, TOO_LOW, TOO_HIGH, /* ... */ }

// Get data
var data: Either<String, ErrCode> = getData()

// Transform left value
// Other methods can be used to transform a Either class.
val transformed: Either<Int?, ErrCode> = data.letLeft { it.toIntOrNull }

// Assert left Int? to Float without exception.
fun toProgress(value: Either<Int?, ErrCode>): Either<Float, ErrCode> {

    // A 'require' block do not allow to be endded.
    // You need to return or throw an exception inside.
    val percent = value.requireLeft { it: Right<ErrCode> ->
        // Here we return the upper function toProgress, early, 
        // with the already known error
        return it
    }

    percent ?: return Right(ErrCode.CONVERT)
    if (percent <= 0) return Right(ErrCode.TOO_LOW)
    if (percent >= 100) return Right(ErrCode.TOO_HIGH)
    return Left(percent.toFloat() / 100f)
}

// Show result
when (val result = toProgress(transformed)) {
    is Left -> println("Current progress ${result.left}")
    is Right -> println("Invalid value. Reason ${result.right}")
}

```

### Option

```kotlin
fun updateInternal(value: String?) {
    //...
}

val data: Option<String?> = getDataFromSomewhere()
val value: String = data.requireValue { error("Failed to fetch value") }
updateInternal(value)
```

The option class is serializable with [kotlinx-serialization](https://github.com/Kotlin/kotlinx.serialization).

Make sure to not encode defaults in your encoder. Like in Json with `{ encodeDefaults = false }`. Then, when defining a
data class, initialize fields to `Empty`:

```kotlin
@Serializable
data class Data(val value: Option<String?> = Empty)
```

Doing it this way allows the deserializer to fall back to `Empty` when the field is not present inside a json object.
If the field is present and null, it deserializes to `Value(null)`

```
Deserialization:
{ }                  => Data(Empty) 
{ "value": null }    => Data(Value(null)) 
{ "value": "value" } => Data(Value("value"))
```

## Api

### Create

```kotlin
var option: Option<String>
option = Value("value")
option = Empty

var either: Either<String, Int>
either = Left("value")
either = Right(1234)
```

### Transformation

#### Either

[//]: # (@formatter:off)
```kotlin
val either: Either<String, Int> = Left("value")

val a: Either<Int, String>  = either.invert() // invert types

val b: String  = either.left        // Might throw an Exception
val c: Int     = either.right       // Might throw an Exception
val d: String? = either.leftOrNull
val e: Int?    = either.rightOrNull

val f: Either<Unit, Int>    = either.letLeft { str: String -> Unit }     // Transform left type
val g: Either<String, Unit> = either.letRight { int: Int -> Unit }       // Transform right type
val h: Either<Unit, Unit>   = either.letBoth(onLeft = {}, onRight = {})  // Transform both types

val i: String = either.foldRight { int: Int -> "new" }     // Transform right value to left type
val j: Int    = either.foldLeft { str: String -> 0 }       // Transform left value to right type
val k: Unit   = either.foldBoth(onLeft = {}, onRight = {}) // Transform both left and right value to same type

assertTrue { i == "value" }
assertTrue { j == 0 }
```
[//]: # (@formatter:on)

#### Option

[//]: # (@formatter:off)
```kotlin
val option: Option<String> = Value("value")

val a: String  = option.value       // Might throw an Exception
val b: String? = option.valueOrNull

val c: Option<Unit> = option.letValue { str: String -> Unit } // Transform value type

val d: String = option.foldEmpty { "new" }              // Create value type on Empty
val e: Unit   = option.foldBoth(onEmpty = {}, onValue = {}) // Transform bot
assertTrue { d == "value" }
```
[//]: # (@formatter:on)

#### Destructuring

You can destructure known types.

```kotlin
val left = Left("value")
val right = Right(1234)
val value = Value(Any())

val (l: String) = left
val (r: Int) = right
val (v: Any) = value
```

### From one to another

#### Either to Option

```kotlin
val either: Either<String, Int> = Left("value")

val a: Option<String> = either.leftAsOption()
val b: Option<Int> = either.rightAsOption()
```

#### Option to Either

```kotlin
val option: Option<String> = Value("value")

val a: Either<String, Int> = option.letEmptyAsRight { 0 }
val b: Either<Int, String> = option.letEmptyAsLeft { 0 }

val c: Either<Unit, Long> = option.letAsLeft(onValue = {}, onEmpty = { 0L })
val d: Either<Unit, Long> = option.letAsRight(onValue = { 0L }, onEmpty = {})
```

### Require values

In a `require` block you either need to return or throw an exception.
You cannot let the code go at the end of the block.

Use destructuring syntax to handle `Either` / `Option` values directly.

#### Either

```kotlin
val either: Either<String, Int> = Left("value")

val a: String = either.requireLeft { value: Right<Int> -> error("") }
val b: String = either.requireLeft { (value: Int) -> error("") }

val c: Int = either.requireRight { value: Left<String> -> error("") }
val d: Int = either.requireRight { (value: String) -> error("") }
```

#### Option

```kotlin
val option: Option<String> = Value("value")

val a: String = option.requireValue { error("Failed, value is empty") }

option.requireEmpty { opt: Option<String> -> return }
option.requireEmpty { (value: String) -> return }
```