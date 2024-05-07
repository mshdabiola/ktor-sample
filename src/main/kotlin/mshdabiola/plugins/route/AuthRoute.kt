package mshdabiola.plugins.route

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import java.security.MessageDigest
import kotlin.text.Charsets.UTF_8

fun Application.basic(){
    install(Authentication){
        basic("authe-basic") {
            realm="access to path / "

            validate {

                hashedUserTable.authenticate(it)
//                if(it.name=="abiola" && it.password=="123456"){
//                    UserIdPrincipal(it.name)
//                }else
//                    null
            }
        }

        digest ("authe-digest"){
            realm= myRealm
            digestProvider{userName, realm ->
                userTable[userName]
            }
            validate {
                if (it.userName.isNotEmpty())
                    CustomPrincipal(it.userName,it.realm)
                else
                    null

            }

        }

        bearer("authe-bearer") {
            realm="access to path / "
            authenticate {
                if (it.token=="123456")
                     UserIdPrincipal("Abiola")
                else
                    null
            }

        }

        form("authe-form"){
            userParamName="username"
            passwordParamName="password"

            validate {
                if (it.name=="abiola"&&it.password=="123456")
                    UserIdPrincipal("Abiola")

                else
                    null
            }

            challenge {
                call.respond(HttpStatusCode.Unauthorized, "Credentials are not valid")
            }
        }

        session<UserSession3>("authe-session"){
            validate {
                this@basic.log.debug("name: ${it.name}")
                if (it.name.contains("Abiola"))
                    it
                else
                    null
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized, "Credentials are not valid")
            }
        }

        jwt(this@basic.environment)

    }
    install(Sessions){
        cookie<UserSession>("user_session"){
            cookie.path="/"
            cookie.maxAgeInSeconds=60
        }


    }

    routing {
        authenticate("authe-basic"){
            get("basic"){
                call.respondText { "User name is ${call.principal<UserIdPrincipal>()?.name}" }
            }
        }

        authenticate("authe-digest"){
            get("digest"){
                call.respondText { "User name is ${call.principal<CustomPrincipal>()?.userName}" }
            }
        }

        authenticate("authe-bearer"){
            get("bearer"){
                call.respondText { "User name is ${call.principal<UserIdPrincipal>()?.name}" }
            }
        }

        authenticate("authe-form"){
            post("form"){
                val user=call.principal<UserIdPrincipal>()!!
              this@basic.log.debug( "User name is ${call.principal<UserIdPrincipal>()?.name}" )
                call.sessions.set(UserSession3(user.name,1))

                call.respondRedirect("/hello")
            }
        }

        authenticate ("authe-session"){
            get ("/hello"){
                val userSession = call.principal<UserSession3>()
                call.sessions.set(userSession?.copy(count = userSession.count + 1))
                call.respondText("Hello, ${userSession?.name}! Visit count is ${userSession?.count}.")
            }
        }




    }
        jwtAuth()
}

val digestFunction = getDigestFunction("SHA-256") { "ktor${it.length}" }
val hashedUserTable = UserHashedTableAuth(
    table = mapOf(
        "jetbrains" to digestFunction("foobar"),
        "admin" to digestFunction("password"),
        "abiola" to digestFunction("123456")
    ),
    digester = digestFunction
)

fun getMd5Digest(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray(UTF_8))

val myRealm = "Access to the '/' path"
val userTable: Map<String, ByteArray> = mapOf(
    "jetbrains" to getMd5Digest("jetbrains:$myRealm:foobar"),
    "admin" to getMd5Digest("admin:$myRealm:password"),
    "abiola" to getMd5Digest("abiola:$myRealm:123456")
)

data class CustomPrincipal(val userName: String, val realm: String) : Principal

data class UserSession3(val name: String, val count: Int) : Principal


