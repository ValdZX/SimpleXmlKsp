package ua.vald_zx.simplexml.ksp

import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.typeOf

@ThreadLocal
object SimpleXml {

    var pretty: Boolean = false

    inline fun <reified T : Any> serialize(obj: T): String {
        val serializer = GlobalSerializersLibrary.findSerializers(T::class)
        val typeArguments = if (serializer.needArguments) {
            typeOf<T>().arguments.map { it.type }
        } else {
            emptyList()
        }
        return serializer.serialize(obj, typeArguments)
    }

    inline fun <reified T : Any> deserialize(xml: String): T {
        val serializer = GlobalSerializersLibrary.findSerializers(T::class)
        val typeArguments = if (serializer.needArguments) {
            typeOf<T>().arguments.map { it.type }
        } else {
            emptyList()
        }
        return serializer.deserialize(xml, typeArguments)
    }
}