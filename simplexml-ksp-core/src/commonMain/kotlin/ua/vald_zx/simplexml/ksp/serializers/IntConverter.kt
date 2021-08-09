package ua.vald_zx.simplexml.ksp.serializers

import ua.vald_zx.simplexml.ksp.Converter

object IntConverter : Converter<Int> {
    override fun write(obj: Int): String {
        return obj.toString()
    }

    override fun read(raw: String): Int {
        return raw.toInt()
    }
}