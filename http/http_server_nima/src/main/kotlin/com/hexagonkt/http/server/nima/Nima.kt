package com.hexagonkt.http.server.nima

import com.hexagonkt.http.server.*
import com.hexagonkt.http.handlers.HandlerBuilder
import com.hexagonkt.http.handlers.HttpHandler

/**
 * Create a Helidon Nima server and start it. It is a shortcut to avoid passing the adapter.
 *
 * @param settings Server settings info .
 * @param handlers List of [HttpHandler] handlers used in this server instance.
 *
 * @return The started [HttpServer] instance.
 */
fun serve(
    settings: HttpServerSettings = HttpServerSettings(), handlers: HttpHandler
): HttpServer =
    HttpServer(NimaServerAdapter(), handlers, settings).apply { start() }

/**
 * Create a Helidon Nima server and start it. It is a shortcut to avoid passing the adapter.
 *
 * @param settings Server settings info.
 * @param block Lambda to be used to create the list of [HttpHandler] handlers used in the server.
 *
 * @return The started [HttpServer] instance.
 */
fun serve(
    settings: HttpServerSettings = HttpServerSettings(), block: HandlerBuilder.() -> Unit
): HttpServer =
    HttpServer(NimaServerAdapter(), HandlerBuilder().apply { block() }.handler(), settings)
        .apply { start() }
