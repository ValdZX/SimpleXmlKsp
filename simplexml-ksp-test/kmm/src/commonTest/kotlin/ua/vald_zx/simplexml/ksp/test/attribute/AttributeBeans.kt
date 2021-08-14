package ua.vald_zx.simplexml.ksp.test.attribute

import ua.vald_zx.simplexml.ksp.Attribute
import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.ElementList
import ua.vald_zx.simplexml.ksp.Path

data class RootAttribute(
    @Attribute
    var attr: String
)

data class PathAttribute(
    @field:[Path("Tag") Attribute]
    var attr: String
)

data class TagAttribute(
    @field:[Path("Tag") Attribute]
    var attr: String,
    @Element(name = "Tag")
    var tag: String
)

//data class SubTagPathAttribute(
//    @field:[Path("Tag/Layer1") Attribute]
//    var attr: String,
//    @Element(name = "Tag")
//    var tag: String
//)

data class ListAttribute(
    @field:[Path("List") Attribute]
    var attr: String,
    @ElementList(name = "List")
    var tag: List<String>
)
