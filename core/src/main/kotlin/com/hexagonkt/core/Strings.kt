package com.hexagonkt.core

import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.lang.System.getProperty
import java.net.InetAddress
import java.net.URI
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Base64
import kotlin.reflect.KClass

private const val VARIABLE_PREFIX = "{{"
private const val VARIABLE_SUFFIX = "}}"

private val base64Encoder: Base64.Encoder by lazy { Base64.getEncoder().withoutPadding() }
private val base64Decoder: Base64.Decoder by lazy { Base64.getDecoder() }

/** Runtime specific end of line. */
val eol: String by lazy { getProperty("line.separator") }

/** Supported types for the [parseOrNull] function. */
val parsedClasses: Set<KClass<*>> by lazy {
    setOf(
        Boolean::class,
        Int::class,
        Long::class,
        Float::class,
        Double::class,
        String::class,
        InetAddress::class,
        URL::class,
        URI::class,
        File::class,
        LocalDate::class,
        LocalTime::class,
        LocalDateTime::class,
    )
}

/**
 * Filter the target string substituting each key by its value. The keys format resembles Mustache's
 * one: `{{key}}` and all occurrences are replaced by the supplied value.
 *
 * If a variable does not have a parameter, it is left as it is.
 *
 * @param parameters The map with the list of key/value tuples.
 * @return The filtered text or the same string if no values are passed or found in the text.
 * @sample com.hexagonkt.core.StringsSamplesTest.filterVarsExample
 */
fun String.filterVars(parameters: Map<*, *>): String =
    this.filter(
        VARIABLE_PREFIX,
        VARIABLE_SUFFIX,
        parameters
            .filterKeys { it != null }
            .map { (k, v) -> k.toString() to v.toString() }
            .toMap()
    )

/**
 * [TODO](https://github.com/hexagonkt/hexagon/issues/271).
 *
 * @receiver .
 * @param prefix .
 * @param suffix .
 * @param parameters .
 * @return .
 */
fun String.filter(prefix: String, suffix: String, parameters: Map<String, *>): String =
    parameters.entries.fold(this) { result, (first, second) ->
        result.replace(prefix + first + suffix, second.toString())
    }

/**
 * Encode the content of this byteArray to base64.
 *
 * @receiver ByteArray to be encoded to base64.
 * @return The base64 encoded string.
 */
fun ByteArray.encodeToBase64(): String =
    base64Encoder.encodeToString(this)

/**
 * Encode this string to base64.
 *
 * @receiver String to be encoded to base64.
 * @return The base64 encoded string.
 */
fun String.encodeToBase64(): String =
    toByteArray().encodeToBase64()

/**
 * Decode this base64 encoded string.
 *
 * @receiver String encoded to base64.
 * @return The ByteArray result of decoding the base64 string.
 */
fun String.decodeBase64(): ByteArray =
    base64Decoder.decode(this)

/**
 * [TODO](https://github.com/hexagonkt/hexagon/issues/271).
 *
 * @receiver .
 * @param T .
 * @param type .
 * @return .
 */
@Suppress("UNCHECKED_CAST") // All allowed types are checked at runtime
fun <T : Any> String.parse(type: KClass<T>): T =
    this.let {
        require(type in parsedClasses) { "Unsupported type: ${type.qualifiedName}" }

        when (type) {
            Boolean::class -> this.toBooleanStrictOrNull()
            Int::class -> this.toIntOrNull()
            Long::class -> this.toLongOrNull()
            Float::class -> this.toFloatOrNull()
            Double::class -> this.toDoubleOrNull()
            String::class -> this
            InetAddress::class -> this.let(InetAddress::getByName)
            URL::class -> this.let(::URL)
            URI::class -> this.let(::URI)
            File::class -> this.let(::File)
            LocalDate::class -> LocalDate.parse(this)
            LocalTime::class -> LocalTime.parse(this)
            LocalDateTime::class -> LocalDateTime.parse(this)
            else -> error("Unsupported type: ${type.qualifiedName}")
        }
    } as T

/**
 * [TODO](https://github.com/hexagonkt/hexagon/issues/271).
 *
 * @receiver .
 * @param T .
 * @param type .
 * @return .
 */
fun <T : Any> String?.parseOrNull(type: KClass<T>): T? =
    this?.let {
        require(type in parsedClasses) { "Unsupported type: ${type.qualifiedName}" }
        try {
            parse(type)
        }
        catch (e: Exception) {
            null
        }
    }

fun String.stripAnsi(): String =
    replace(Ansi.REGEX, "")

/**
 * [TODO](https://github.com/hexagonkt/hexagon/issues/271).
 *
 * @receiver .
 * @return .
 */
fun String.toStream(): InputStream =
    ByteArrayInputStream(this.toByteArray())

/**
 * [TODO](https://github.com/hexagonkt/hexagon/issues/271).
 *
 * @receiver .
 * @param count .
 * @param pad .
 * @return .
 */
fun String.prependIndent(count: Int = 4, pad: String = " "): String =
    this.prependIndent(pad.repeat(count))
