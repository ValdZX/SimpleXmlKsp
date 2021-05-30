package ua.vald_zx.simplexml.ksp.sample

import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.Path

data class XmlBean(
    @Element("Element")
    var requiredElement: String = "",
    @Path("Auth")
    @Element("ElementWithPath", false)
    var elementWithPath: String = "",
    @field:[Path("Auth") Element("Field", false)]
    var field: String = ""
)