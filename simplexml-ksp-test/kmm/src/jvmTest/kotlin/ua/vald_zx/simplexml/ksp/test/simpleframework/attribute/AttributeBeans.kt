package ua.vald_zx.simplexml.ksp.test.simpleframework.attribute

import org.simpleframework.xml.*

@Root(name = "RootAttribute")
data class RootAttribute(
    @field:Attribute(name = "attr")
    var attr: String? = null
)

class PathAttribute {
    @field:[Path("Tag") Attribute]
    var attr: String? = null
}

data class TagAttribute(
    @field:[Path("Tag") Attribute]
    var attr: String? = null,
    @field:Element(name = "Tag")
    var tag: String? = null
)

class ListAttribute {
    @field:[Path("List") Attribute]
    var attr: String? = null

    @field:ElementList(name = "List")
    var tag: List<String>? = null
}

data class NullableTagAttribute(
    @field:[Path("Tag") Attribute]
    var attr: String? = null,
    @field:Element(name = "Tag", required = false)
    var tag: String? = null
)