package ua.vald_zx.simplexml.ksp

import kotlin.reflect.KClass

object GlobalSerializersLibrary {
    private val library: MutableMap<KClass<*>, () -> Serializer<*>> = mutableMapOf()

    fun <T : Any> add(clazz: KClass<T>, serializerProvider: () -> Serializer<T>) {
        library[clazz] = serializerProvider
    }

    fun <T : Any> findSerializers(clazz: KClass<T>): Serializer<T> {
        @Suppress("UNCHECKED_CAST")
        return library[clazz]?.invoke() as Serializer<T>? ?: error("Serializer not found")
    }
}