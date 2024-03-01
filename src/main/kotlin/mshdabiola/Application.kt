package mshdabiola

import io.ktor.server.application.*
import mshdabiola.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
}
