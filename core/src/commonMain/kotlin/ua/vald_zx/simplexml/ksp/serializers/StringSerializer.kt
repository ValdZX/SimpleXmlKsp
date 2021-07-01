package ua.vald_zx.simplexml.ksp.serializers

import ua.vald_zx.simplexml.ksp.Serializer

class StringSerializer : Serializer<String> {
    override fun serialize(obj: String): String = obj

    override fun deserialize(raw: String): String = raw
}