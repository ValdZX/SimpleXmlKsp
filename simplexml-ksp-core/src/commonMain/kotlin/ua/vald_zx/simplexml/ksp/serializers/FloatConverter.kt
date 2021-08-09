package ua.vald_zx.simplexml.ksp.serializers

import ua.vald_zx.simplexml.ksp.Converter

object FloatConverter : Converter<Float> {
    override fun write(obj: Float): String {
        return obj.toString()
    }

    override fun read(raw: String): Float {
        return raw.toFloat()
    }
}