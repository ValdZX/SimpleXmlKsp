package ua.vald_zx.simplexml.ksp.serializers

import ua.vald_zx.simplexml.ksp.Serializer

class FloatSerializer : Serializer<Float> {
    override fun serialize(obj: Float): String {
        return obj.toString()
    }

    override fun deserialize(raw: String): Float {
        return raw.toFloat()
    }
}