package ua.vald_zx.simplexml.ksp.test.benchmark

import ua.vald_zx.simplexml.ksp.*
import ua.vald_zx.simplexml.ksp.test.converter.TestDoubleConverter
import ua.vald_zx.simplexml.ksp.test.converter.TestStringConverter

class PathAttributeG {
    @field:[Path("Tag") Attribute]
    var attr: String? = null
}

class RequiredFieldG {
    @Element(required = true)
    var tag: String = ""
}

class NullableFieldMutableListOfStringsG {
    @ElementList(name = "List", entry = "Entry")
    var list: MutableList<String>? = null
}

@Root("resources")
class FieldAndroidStringResourcesG {
    @ElementMap(inline = true, key = "name", entry = "string", attribute = true)
    var resources: MutableMap<String, String>? = null
}

class MultipleConvertersConstructorFieldG {
    @field:[Element Convert(TestStringConverter::class)]
    var tag1: String? = null

    @field:[Element Convert(TestDoubleConverter::class)]
    var tag2: Double? = null
}