package ua.vald_zx.simplexml.ksp

object SimpleXml {
    inline fun <reified T : Any> serialize(obj: T): String {
        return GlobalSerializersLibrary.findSerializers(T::class).serialize(obj)
    }

    inline fun <reified T : Any> deserialize(xml: String): T {
        return GlobalSerializersLibrary.findSerializers(T::class).deserialize(xml)
    }
}