package ua.vald_zx.xml.sample

import ua.vald_zx.xml.Element
import ua.vald_zx.xml.Path

data class XmlBean(
    @Element("Element")
    var requiredElement: String = "",
    @Path("Auth")
    @Element("ElementWithPath", false)
    var elementWithPath: String = "",
    @field:[Path("Auth") Element("Field", false)]
    var field: String = ""
)