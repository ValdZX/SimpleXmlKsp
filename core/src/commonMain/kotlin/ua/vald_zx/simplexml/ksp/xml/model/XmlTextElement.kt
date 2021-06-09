package ua.vald_zx.simplexml.ksp.xml.model

class XmlTextElement(parent: XmlElement?, override val text: String) : XmlElement("", parent) {
    override fun toString(): String {
        return "XmlTextElement[$text]"
    }
}