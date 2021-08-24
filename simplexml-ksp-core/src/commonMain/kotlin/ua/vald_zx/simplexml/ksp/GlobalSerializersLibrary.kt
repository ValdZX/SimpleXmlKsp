package ua.vald_zx.simplexml.ksp

import ua.vald_zx.simplexml.ksp.serializers.*
import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.KClass

@ThreadLocal
object GlobalSerializersLibrary {
    private val library: MutableMap<KClass<*>, () -> Serializer<*>> by lazy {
        mutableMapOf(
            String::class to { ValueSerializer(StringConverter) },
            Int::class to { ValueSerializer(IntConverter) },
            Double::class to { ValueSerializer(DoubleConverter) },
            Float::class to { ValueSerializer(FloatConverter) },
            Long::class to { ValueSerializer(LongConverter) },
            Short::class to { ValueSerializer(ShortConverter) },
            Char::class to { ValueSerializer(CharConverter) },
        )
    }

    fun <T : Any> add(clazz: KClass<T>, serializerProvider: () -> Serializer<T>) {
        library[clazz] = serializerProvider
    }

    fun <T : Any> findSerializers(clazz: KClass<T>): Serializer<T> {
        @Suppress("UNCHECKED_CAST")
        return library[clazz]?.invoke() as Serializer<T>? ?: error("Serializer not found for $clazz")
    }
}