package ua.vald_zx.simplexml.ksp

import kotlin.reflect.KClass

object GlobalSerializersLibrary {
    private val library: MutableMap<KClass<*>, Serializer<*>> = mutableMapOf()

    fun <T : Any> add(clazz: KClass<T>, serializer: Serializer<T>) {
        library[clazz] = serializer
    }

    fun <T : Any> findSerializers(clazz: KClass<T>): Serializer<T> {
        @Suppress("UNCHECKED_CAST")
        return library[clazz] as Serializer<T>? ?: error("Serializer not found")
    }
}