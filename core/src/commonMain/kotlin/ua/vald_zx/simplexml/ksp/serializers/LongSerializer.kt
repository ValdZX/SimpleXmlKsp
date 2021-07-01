package ua.vald_zx.simplexml.ksp.serializers

import ua.vald_zx.simplexml.ksp.Serializer

class LongSerializer : Serializer<Long> {
    override fun serialize(obj: Long): String {
        return obj.toString()
    }

    override fun deserialize(raw: String): Long {
        return raw.toLong()
    }
}