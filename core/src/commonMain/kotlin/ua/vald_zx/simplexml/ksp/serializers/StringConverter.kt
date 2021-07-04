package ua.vald_zx.simplexml.ksp.serializers

import ua.vald_zx.simplexml.ksp.Converter

object StringConverter : Converter<String> {
    override fun write(obj: String): String {
        return obj
    }

    override fun read(raw: String): String {
        return raw
    }
}