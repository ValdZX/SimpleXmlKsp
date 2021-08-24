package ua.vald_zx.simplexml.ksp.test.converter

import ua.vald_zx.simplexml.ksp.Converter

class TestStringConverter : Converter<String> {
    override fun write(obj: String): String {
        return "~~~$obj~~~"
    }

    override fun read(raw: String): String {
        return raw.removePrefix("~~~").removeSuffix("~~~")
    }
}

class TestDoubleConverter : Converter<Double> {
    override fun write(obj: Double): String {
        return obj.times(2.0).toLong().toString()
    }

    override fun read(raw: String): Double {
        return raw.toLong().div(2.0)
    }
}