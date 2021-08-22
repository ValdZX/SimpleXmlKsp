package ua.vald_zx.simplexml.ksp

import kotlin.reflect.typeOf

@OptIn(ExperimentalStdlibApi::class)
object SimpleXml {
    var pretty: Boolean = false

    inline fun <reified T : Any> serialize(obj: T): String {
        val typeArguments = typeOf<T>().arguments
        return GlobalSerializersLibrary.findSerializers(T::class).serialize(obj, typeArguments)
    }

    inline fun <reified T : Any> deserialize(xml: String): T {
        val typeArguments = typeOf<T>().arguments
        return GlobalSerializersLibrary.findSerializers(T::class).deserialize(xml, typeArguments)
    }
}