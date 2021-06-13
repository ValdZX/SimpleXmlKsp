package ua.vald_zx.simplexml.ksp.xml.parsing

import ua.vald_zx.simplexml.ksp.xml.model.XmlElement
import ua.vald_zx.simplexml.ksp.xml.model.XmlTextElement

internal class DomBuilder : EventParser {
    var root: XmlElement? = null
        private set
    private var current: XmlElement? = null
    override fun startNode(name: String, attrs: MutableMap<String, String>) {
        val tmp = XmlElement(name, current, attrs)
        if (current != null) current?.appendChild(tmp) else this.root = tmp
        current = tmp
    }

    override fun endNode() {
        current = current?.parent
    }

    override fun someText(txt: String) {
        if (txt.isEmpty()) return
        current?.children?.add(XmlTextElement(current, txt.trim { it <= ' ' }))
    }
}