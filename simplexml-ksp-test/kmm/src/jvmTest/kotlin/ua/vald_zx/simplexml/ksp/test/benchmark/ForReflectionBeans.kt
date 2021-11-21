package ua.vald_zx.simplexml.ksp.test.benchmark

import org.simpleframework.xml.*
import org.simpleframework.xml.convert.Convert
import org.simpleframework.xml.convert.Converter
import org.simpleframework.xml.stream.InputNode
import org.simpleframework.xml.stream.OutputNode

class PathAttributeR {
    @field:[Path("Tag") Attribute]
    var attr: String? = null
}

class RequiredFieldR {
    @field:Element(required = true)
    var tag: String = ""
}

class NullableFieldMutableListOfStringsR {
    @field:ElementList(name = "List", entry = "Entry")
    var list: MutableList<String>? = null
}

@Root(name = "resources")
class FieldAndroidStringResourcesR {
    @field:ElementMap(inline = true, key = "name", entry = "string", attribute = true)
    var resources: MutableMap<String, String>? = null
}

class TestStringConverter : Converter<String> {

    override fun write(node: OutputNode?, value: String?) {
        node?.value = "~~~$value~~~"
    }

    override fun read(node: InputNode?): String {
        return node?.value?.removePrefix("~~~")?.removeSuffix("~~~") ?: ""
    }
}

class TestDoubleConverter : Converter<Double> {

    override fun write(node: OutputNode?, value: Double?) {
        node?.value = value?.times(2.0)?.toLong()?.toString()
    }

    override fun read(node: InputNode?): Double {
        return node?.value?.toLong()?.div(2.0) ?: 0.0
    }
}

class MultipleConvertersConstructorFieldR {
    @field:[Element Convert(TestStringConverter::class)]
    var tag1: String? = null

    @field:[Element Convert(TestDoubleConverter::class)]
    var tag2: Double? = null
}