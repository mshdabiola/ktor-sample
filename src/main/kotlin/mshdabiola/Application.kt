package mshdabiola

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.get
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.p
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import mshdabiola.plugins.*
import mshdabiola.plugins.route.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(Resources)
    install(AutoHeadResponse)
    install(RequestValidation){
        validate<String> {
            log.debug("request string $it")
            if (it.contains("abiola")){
                ValidationResult.Invalid("Contain biola")

            }else{
                ValidationResult.Valid
            }
        }
    }
    install(StatusPages){
        exception <RequestValidationException>{ call, requestValidationException ->
            call.respond(HttpStatusCode.BadRequest,requestValidationException.reasons.joinToString())
        }
    }
    install(ContentNegotiation) {
        json()
    }



//    webRoute()
    resourceRoute()
    staticRoute()
    dynamicRoute()
    basic()
}


