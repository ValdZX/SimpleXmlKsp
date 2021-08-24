package ua.vald_zx.simplexml.ksp.serializers

import ua.vald_zx.simplexml.ksp.Converter

object DoubleConverter : Converter<Double> {
    override fun write(obj: Double): String {
        return if ((obj % 1.0) == 0.0) {
            obj.toLong().toString()
        } else {
            obj.toString()
        }
    }

    override fun read(raw: String): Double {
        return raw.toDouble()
    }
}