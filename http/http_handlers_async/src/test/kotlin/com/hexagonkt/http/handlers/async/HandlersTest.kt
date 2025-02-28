package com.hexagonkt.http.handlers.async

import com.hexagonkt.handlers.async.done
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.reflect.KClass

internal class HandlersTest {

    @Test fun `Root path is created properly from a list of handlers`() {
        assertEquals(PathHandler(""), path(handlers = emptyList()))
        assertEquals(PathHandler("/root"), path(handlers = listOf(PathHandler("/root"))))

        val expected = PathHandler("", listOf(OnHandler("/on") { this.done() }))
        val actual = path(handlers = listOf(OnHandler("/on") { this.done() }))
        assertEquals(expected.predicate, actual.predicate)
        assertEquals(expected.handlersPredicates(), actual.handlersPredicates())

        val expected2 = PathHandler("", listOf(OnHandler("/a") { this.done() }, OnHandler("/b") { this.done() }))
        val actual2 = path(handlers = listOf(OnHandler("/a") { this.done() }, OnHandler("/b") { this.done() }))
        assertEquals(expected2.predicate, actual2.predicate)
        assertEquals(expected2.handlersPredicates(), actual2.handlersPredicates())
    }

    @Test fun `Root path is created properly from a list of handlers and a prefix`() {
        assertEquals(PathHandler("/prefix"), path("/prefix", emptyList()))
        assertEquals(PathHandler("/prefix/root"), path("/prefix", listOf(PathHandler("/root"))))

        val expected = PathHandler("/prefix", listOf(OnHandler("/on") { this.done() }))
        val actual = path("/prefix", listOf(OnHandler("/on") { this.done() }))
        assertEquals(expected.predicate, actual.predicate)
        assertEquals(expected.handlersPredicates(), actual.handlersPredicates())

        val expected2 = PathHandler(
            "/prefix",
            listOf(OnHandler("/a") { this.done() }, OnHandler("/b") { this.done() })
        )
        val actual2 = path("/prefix", listOf(OnHandler("/a") { this.done() }, OnHandler("/b") { this.done() }))
        assertEquals(expected2.predicate, actual2.predicate)
        assertEquals(expected2.handlersPredicates(), actual2.handlersPredicates())
    }

    @Test
    @Suppress("CAST_NEVER_SUCCEEDS") // Required for test 'null' arguments
    fun `Exceptions are casted properly`() {
        assertFailsWith<IllegalStateException> { null.castException(Exception::class) }
        assertFailsWith<IllegalStateException> { null.castException(null as? KClass<Exception>) }
        assertFailsWith<ClassCastException> {
            IllegalStateException().castException(IllegalArgumentException::class)
        }
        assertFailsWith<IllegalStateException> {
            RuntimeException().castException(null as? KClass<Exception>)
        }

        val ise = IllegalStateException()
        assertEquals(ise, ise.castException(RuntimeException::class))
    }

    private fun PathHandler.handlersPredicates(): List<HttpPredicate> =
        handlers.map { it.handlerPredicate }
}
