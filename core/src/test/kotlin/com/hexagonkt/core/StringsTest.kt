package com.hexagonkt.core

import java.io.File
import java.lang.IllegalArgumentException
import java.net.InetAddress
import java.net.URI
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.*
import kotlin.text.prependIndent

internal class StringsTest {

    @Test fun `Filter variables returns the given string if no parameters are set`() {
        val template = "User #{user}"

        assertEquals(template, template.filterVars(mapOf<Any, Any>()))
    }

    @Test fun `Filter variables returns the same string if no variables are defined in it`() {
        val template = "User no vars"

        assertEquals(template, template.filterVars(mapOf("vars" to "value")))
        assertEquals(template, template.filterVars(mapOf<Any, Any>()))
    }

    @Test fun `Filter variables returns the same string if variable values are not found`() {
        val template = "User #{user}"

        assertEquals(template, template.filterVars(mapOf("key" to "value")))
    }

    @Test fun `Filter variables ignores empty parameters`() {
        val result = "{{email}}: User {{user}} aka {{user}} <{{email}}>".filterVars(
            mapOf(
                null to "Void",
                "" to "John",
                "email" to "john@example.co"
            )
        )

        assertEquals("john@example.co: User {{user}} aka {{user}} <john@example.co>", result)
    }

    @Test fun `Filter variables replaces all occurrences of variables with their values`() {
        val result = "{{email}}: User {{user}} aka {{user}} <{{email}}>".filterVars(
            mapOf(
                "user" to "John",
                "email" to "john@example.co"
            )
        )

        assertEquals("john@example.co: User John aka John <john@example.co>", result)
    }

    @Test fun `Filter returns the given string if no parameters are set`() {
        val template = "User #{user}"

        assertEquals(template, template.filter("#{", "}", emptyMap<String, Any>()))
    }

    @Test fun `Filter returns the same string if no variables are defined in it`() {
        val template = "User no vars"

        assertEquals(template, template.filter("#{", "}", emptyMap<String, Any>()))
        assertEquals(template, template.filter("#{", "}", mapOf("vars" to "value")))
    }

    @Test fun `Filter returns the same string if variable values are not found`() {
        val template = "User #{user}"

        assertEquals(template, template.filter("#{", "}", mapOf("key" to "value")))
    }

    @Test fun `Filter ignores empty parameters`() {
        val result = "{{email}}: User {{user}} aka {{user}} <{{email}}>".filter(
            "{{", "}}",
            mapOf(
                "" to "John",
                "email" to "john@example.co"
            )
        )

        assertEquals("john@example.co: User {{user}} aka {{user}} <john@example.co>", result)
    }

    @Test fun `Filter replaces all occurrences of variables with their values`() {
        val result = "{{email}}: User {{user}} aka {{user}} <{{email}}>".filter(
            "{{", "}}",
            mapOf(
                "user" to "John",
                "email" to "john@example.co"
            )
        )

        assertEquals("john@example.co: User John aka John <john@example.co>", result)
    }

    @Test fun `Parsed classes are the ones supported by parseOrNull`() {
        val tests = mapOf(
            Boolean::class to "true",
            Int::class to "1",
            Long::class to "2",
            Float::class to "3.2",
            Double::class to "4.3",
            String::class to "text",
            InetAddress::class to "127.0.0.1",
            URL::class to "http://example.com",
            URI::class to "schema://host:0/file",
            File::class to "/absolute/file.txt",
            LocalDate::class to "2020-12-31",
            LocalTime::class to "23:59",
            LocalDateTime::class to "2021-11-21T22:45:30",
        )

        assertEquals(parsedClasses, tests.keys)
        tests.forEach { (k, v) -> assertNotNull(v.parseOrNull(k)) }
    }

    @Test fun `Invalid types return 'null' on parse`() {
        val e = assertFailsWith<IllegalArgumentException> { "28".parse(System::class) }
        assertEquals("Unsupported type: java.lang.System", e.message)
    }

    @Test fun `Invalid string transformations return null`() {
        assertNull("2020-02-28".parseOrNull(InetAddress::class))
        assertNull("2020-02-28".parseOrNull(URL::class))
    }

    @Test fun `String transformations work properly`() {
        assertEquals(File("dir/f.txt"), "dir/f.txt".parseOrNull(File::class))
        assertEquals(LocalDate.parse("2020-02-28"), "2020-02-28".parseOrNull(LocalDate::class))
        assertEquals(LocalTime.parse("21:20:10"), "21:20:10".parseOrNull(LocalTime::class))
        assertEquals(
            LocalDateTime.parse("2020-02-28T21:20:10"),
            "2020-02-28T21:20:10".parseOrNull(LocalDateTime::class)
        )
    }

    @Test fun `Data can be encoded and decoded from to base64`() {
        val data = "abcDEF"
        val base64Data = data.encodeToBase64()
        val decodedData = base64Data.decodeBase64()

        assertEquals(data, String(decodedData))
    }

    @Test fun `toStream works as expected`() {
        val s = "alfa-beta-charlie"
        val striped = s.toStream().readAllBytes()
        assertContentEquals(striped, s.toByteArray())
    }

    @Test fun `Indent works as expected`() {
        assertEquals("     text ", " text ".prependIndent())
        assertEquals(" text ", " text ".prependIndent(0))
        assertEquals(" text ", " text ".prependIndent(0, "·"))
        assertEquals("  text ", " text ".prependIndent(1))
        assertEquals(" text ", " text ".prependIndent(1, ""))
        assertEquals("· text ", " text ".prependIndent(1, "·"))
        assertEquals("·· text ", " text ".prependIndent(2, "·"))
        assertEquals("·* text ", " text ".prependIndent(1, "·*"))
        assertEquals("·*·* text ", " text ".prependIndent(2, "·*"))
        assertEquals("·*·*·*text ", "·*text ".prependIndent(2, "·*"))

        assertEquals("    line 1\n    line 2", "line 1\nline 2".prependIndent())
        assertEquals("line 1\nline 2", "line 1\nline 2".prependIndent(0))
        assertEquals("line 1\nline 2", "line 1\nline 2".prependIndent(0, "·"))
        assertEquals(" line 1\n line 2", "line 1\nline 2".prependIndent(1))
        assertEquals("line 1\nline 2", "line 1\nline 2".prependIndent(1, ""))
        assertEquals("·line 1\n·line 2", "line 1\nline 2".prependIndent(1, "·"))
        assertEquals("··line 1\n··line 2", "line 1\nline 2".prependIndent(2, "·"))
        assertEquals("·*line 1\n·*line 2", "line 1\nline 2".prependIndent(1, "·*"))
        assertEquals("·*·*line 1\n·*·*line 2", "line 1\nline 2".prependIndent(2, "·*"))
        assertEquals("·*·*·*line 1\n·*·*·*line 2", "·*line 1\n·*line 2".prependIndent(2, "·*"))
    }

    @Test fun `ANSI testing`() {
        val message = "${Ansi.RED_BG}${Ansi.BRIGHT_WHITE}${Ansi.UNDERLINE}ANSI${Ansi.RESET} normal"
        val noAnsiMessage = message.stripAnsi()
        assertNotEquals(message, noAnsiMessage)
        assertContentEquals(noAnsiMessage.toByteArray(), "ANSI normal".toByteArray())
    }
}
