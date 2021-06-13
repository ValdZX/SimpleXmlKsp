package ua.vald_zx.simplexml.ksp.xml.model

open class XmlElement(
    var name: String,
    var parent: XmlElement? = null,
    var attributes: MutableMap<String, String> = mutableMapOf(),
    var children: MutableList<XmlElement> = mutableListOf()
) {
    fun appendChild(child: XmlElement) {
        children.add(child)
    }

    fun findChildForName(name: String, defaultValue: XmlElement?): XmlElement? {
        return findChildForName(this, name, defaultValue)
    }

    fun numChildrenWithName(name: String): Int {
        var num = 0
        for (child in children) {
            if (name == child.name) num++
        }
        return num
    }

    open val text: String
        get() {
            val builder = StringBuilder()
            for (child in children) {
                if (child is XmlTextElement) builder.append(child.text)
            }
            return if (builder.isEmpty()) "" else builder.toString()
        }

    fun setText(text: String) {
        children.removeAll { xmlElement -> xmlElement is XmlTextElement }
        children.add(XmlTextElement(this, text))
    }

    fun hasNonTextChildren(): Boolean {
        if (children.isEmpty()) return false
        for (e in children) {
            if (e is XmlTextElement) continue
            return true
        }
        return false
    }

    fun getElementsByTagName(name: String): List<XmlElement> {
        val list: MutableList<XmlElement> = ArrayList()
        getElementsByTagName(this, name, list)
        return list
    }

    fun getAll(tagName: String): List<XmlElement> {
        return this.children.filter { it.name == tagName }
    }

    operator fun get(tagName: String): XmlElement {
        return this.children.first { it.name == tagName }
    }

    override fun toString(): String {
        return "XmlElement[$name]"
    }

    class XmlElementBuilder(name: String) {
        private val element = XmlElement(name)
        fun attribute(name: String, value: String): XmlElementBuilder {
            element.attributes[name] = value
            return this
        }

        fun text(text: String): XmlElementBuilder {
            element.setText(text)
            return this
        }

        fun child(child: XmlElement): XmlElementBuilder {
            element.children.add(child)
            child.parent = element
            return this
        }

        fun child(builder: XmlElementBuilder): XmlElementBuilder {
            return child(builder.build())
        }

        fun build(): XmlElement {
            return element
        }

    }

    companion object {
        fun findChildForName(
            element: XmlElement?,
            name: String,
            defaultValue: XmlElement?
        ): XmlElement? {
            if (element == null) return defaultValue
            for (child in element.children) {
                if (name == child.name) return child
            }
            return defaultValue
        }

        private fun getElementsByTagName(
            element: XmlElement,
            name: String,
            list: MutableList<XmlElement>
        ) {
            if (name == element.name) list.add(element)
            for (child in element.children) {
                getElementsByTagName(child, name, list)
            }
        }

        fun newElement(name: String): XmlElementBuilder {
            return XmlElementBuilder(name)
        }
    }
}