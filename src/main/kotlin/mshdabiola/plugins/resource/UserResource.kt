package mshdabiola.plugins.resource

import io.ktor.resources.*


@Resource("users")
 class UserResource(val sort:String?="new"){

     @Resource("new")
     class New(val parent:UserResource)

    @Resource("{id}")
    class Id(val parent: UserResource,val id :Int){
        @Resource("edit")
        class Edit(val parent: Id)
    }

 }