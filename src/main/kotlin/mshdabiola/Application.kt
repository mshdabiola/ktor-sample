package mshdabiola

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import mshdabiola.plugins.*
import mshdabiola.plugins.route.dynamicRoute
import mshdabiola.plugins.route.resourceRoute
import mshdabiola.plugins.route.staticRoute
import mshdabiola.plugins.route.webRoute

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
}
