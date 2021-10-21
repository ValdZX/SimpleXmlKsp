package ua.vald_zx.simplexml.ksp

import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.typeOf

@OptIn(ExperimentalStdlibApi::class)
@ThreadLocal
object SimpleXml {
    var pretty: Boolean = false

    inline fun <reified T : Any> serialize(obj: T): String {
        val typeArguments = typeOf<T>().arguments.map { it.type }
        return GlobalSerializersLibrary.findSerializers(T::class).serialize(obj, typeArguments)
    }

    inline fun <reified T : Any> deserialize(xml: String): T {
        val typeArguments = typeOf<T>().arguments.map { it.type }
        return GlobalSerializersLibrary.findSerializers(T::class).deserialize(xml, typeArguments)
    }
}