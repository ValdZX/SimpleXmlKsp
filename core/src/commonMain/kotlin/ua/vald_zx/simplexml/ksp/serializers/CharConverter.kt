package ua.vald_zx.simplexml.ksp.serializers

import ua.vald_zx.simplexml.ksp.Converter

object CharConverter : Converter<Char> {
    override fun write(obj: Char): String {
        return obj.toString()
    }

    override fun read(raw: String): Char {
        return raw.toCharArray().first()
    }
}