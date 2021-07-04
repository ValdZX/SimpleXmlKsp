package ua.vald_zx.simplexml.ksp.serializers

import ua.vald_zx.simplexml.ksp.Converter

object ShortConverter : Converter<Short> {
    override fun write(obj: Short): String {
        return obj.toString()
    }

    override fun read(raw: String): Short {
        return raw.toShort()
    }
}