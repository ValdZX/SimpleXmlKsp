package ua.vald_zx.simplexml.ksp.xml.parsing

import ua.vald_zx.simplexml.ksp.xml.model.TagXmlElement
import ua.vald_zx.simplexml.ksp.xml.model.TextXmlElement

internal class DomBuilder : EventParser {
    var root: TagXmlElement? = null
        private set
    private var current: TagXmlElement? = null
    override fun startNode(name: String, attrs: MutableMap<String, String>) {
        val tmp = TagXmlElement(name, current, attrs)
        if (current != null) current?.appendChild(tmp) else this.root = tmp
        current = tmp
    }

    override fun endNode() {
        current = current?.parent
    }

    override fun someText(txt: String) {
        if (txt.isEmpty()) return
        current?.children?.add(TextXmlElement(current, txt.trim { it <= ' ' }))
    }
}