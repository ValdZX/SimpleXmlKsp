package ua.vald_zx.simplexml.ksp.serializers

import ua.vald_zx.simplexml.ksp.Serializer

class DoubleSerializer : Serializer<Double> {
    override fun serialize(obj: Double): String {
        return obj.toString()
    }

    override fun deserialize(raw: String): Double {
        return raw.toDouble()
    }
}