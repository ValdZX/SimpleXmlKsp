package ua.vald_zx.simplexml.ksp.serializers

import ua.vald_zx.simplexml.ksp.Serializer

class IntSerializer : Serializer<Int> {
    override fun serialize(obj: Int): String {
        return obj.toString()
    }

    override fun deserialize(raw: String): Int {
        return raw.toInt()
    }
}