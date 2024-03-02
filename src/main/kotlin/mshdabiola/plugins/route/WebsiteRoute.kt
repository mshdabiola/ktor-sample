package mshdabiola.plugins.route

import freemarker.cache.ClassTemplateLoader
import freemarker.core.HTMLOutputFormat
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import mshdabiola.plugins.resource.UserResource
import java.io.File

fun Application.webRoute(){

    routing {
        get("index"){


            call.respondText {
                "Index file parameter ${call.request.queryParameters["price"] ?: "no price"}"
            }
        }
        get("index/{id}"){
            val para=call.parameters["id"] ?: "Nothing"

            call.respondText { "Id is $para" }
        }
        get("/") {
            call.respondText("Hello World!")
        }

        post("text"){


            val text= call.receive<String>()

            call.respondText { "read channel: $text" }
        }

        post("signup"){
            val parameters=call.receiveParameters()

            val username=parameters["username"].toString()

            call.respondText { "The $username account is created" }
        }

        post("upload"){
            val form = call.receiveMultipart()

            var name =""
            var fileName=""

            form.forEachPart {
                when(it){
                    is PartData.FormItem->{
                        name=it.value

                    }
                    is PartData.FileItem->{
                        fileName=it.originalFileName ?:""
                        val fileByte=it.streamProvider().readBytes()
                        val dir =File("./upload")
                        if (dir.exists().not()){
                            dir.mkdirs()
                        }
                       val file= File(dir,fileName)

                           file.createNewFile()
                           file.writeBytes(fileByte)

                    }
                    is PartData.BinaryItem->{}
                    is PartData.BinaryChannelItem->{

                    }
                }
            }

            call.respondText { "name: $name file: $fileName" }

        }
    }
}


fun Application.resourceRoute(){
    routing {
        get<UserResource> {
            call.respondText {
                val uri=call.request.uri
                "sort is ${it.sort} uri is ${call.request.local.localHost}"
            }
        }

        get <UserResource.Id>{
            val params=call.parameters["id"]
            call.respondText {
                "id is ${it.id} parameter is $params"
            }
        }
    }
}



fun Application.staticRoute(){
    routing {
        staticResources("/", "static") {
            default("index.html")
            preCompressed(CompressedFileType.GZIP)
        }

    }
}


fun Application.dynamicRoute(){
    install(FreeMarker){
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        outputFormat = HTMLOutputFormat.INSTANCE

    }
    routing {
        get("news") {

                call.respond(FreeMarkerContent("index.ftl", mapOf("articles" to articles)))

        }

    }
}