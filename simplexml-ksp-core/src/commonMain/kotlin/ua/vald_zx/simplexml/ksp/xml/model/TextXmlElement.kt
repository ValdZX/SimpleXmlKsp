package ua.vald_zx.simplexml.ksp.xml.model

class TextXmlElement(parent: TagXmlElement?, override val text: String) : TagXmlElement("", parent) {
    override fun toString(): String {
        return "XmlTextElement[$text]"
    }
}