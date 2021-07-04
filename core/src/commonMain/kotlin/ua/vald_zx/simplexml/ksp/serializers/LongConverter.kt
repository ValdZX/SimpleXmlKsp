package ua.vald_zx.simplexml.ksp.serializers

import ua.vald_zx.simplexml.ksp.Converter

object LongConverter : Converter<Long> {
    override fun write(obj: Long): String {
        return obj.toString()
    }

    override fun read(raw: String): Long {
        return raw.toLong()
    }
}