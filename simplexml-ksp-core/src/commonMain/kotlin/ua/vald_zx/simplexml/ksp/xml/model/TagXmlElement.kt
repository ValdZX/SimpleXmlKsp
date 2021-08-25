package ua.vald_zx.simplexml.ksp.xml.model

import kotlin.math.min

open class TagXmlElement(
    override var name: String,
    var parent: TagXmlElement? = null,
    var attributes: MutableMap<String, String> = mutableMapOf(),
    var children: MutableList<TagXmlElement> = mutableListOf()
) : XmlElement {
    fun appendChild(child: TagXmlElement) {
        children.add(child)
    }

    fun findChildForName(name: String, defaultValue: TagXmlElement?): TagXmlElement? {
        return findChildForName(this, name, defaultValue)
    }

    fun numChildrenWithName(name: String): Int {
        var num = 0
        for (child in children) {
            if (name == child.name) num++
        }
        return num
    }

    override val text: String
        get() {
            val builder = StringBuilder()
            for (child in children) {
                if (child is TextXmlElement) builder.append(child.text)
            }
            return if (builder.isEmpty()) "" else builder.toString()
        }

    fun setText(text: String) {
        children.removeAll { xmlElement -> xmlElement is TextXmlElement }
        children.add(TextXmlElement(this, text))
    }

    fun hasNonTextChildren(): Boolean {
        if (children.isEmpty()) return false
        for (e in children) {
            if (e is TextXmlElement) continue
            return true
        }
        return false
    }

    fun getElementsByTagName(name: String): List<TagXmlElement> {
        val list: MutableList<TagXmlElement> = ArrayList()
        getElementsByTagName(this, name, list)
        return list
    }

    fun getAll(tagName: String): List<TagXmlElement> {
        return this.children.filter { it.name == tagName }
    }

    operator fun get(tagName: String): TagXmlElement? {
        return this.children.firstOrNull { it.name == tagName }
    }

    fun getPairs(key: String, value: String): List<Pair<TagXmlElement, TagXmlElement>> {
        val keys = getAll(key)
        val values = getAll(value)
        return (0 until min(keys.size, values.size)).map { index ->
            keys[index] to values[index]
        }
    }

    override fun toString(): String {
        return "XmlElement[$name]"
    }

    fun attribute(name: String): AttributeXmlElement {
        return AttributeXmlElement(name, attributes[name].orEmpty())
    }

    class XmlElementBuilder(name: String) {
        private val element = TagXmlElement(name)
        fun attribute(name: String, value: String): XmlElementBuilder {
            element.attributes[name] = value
            return this
        }

        fun text(text: String): XmlElementBuilder {
            element.setText(text)
            return this
        }

        fun child(child: TagXmlElement): XmlElementBuilder {
            element.children.add(child)
            child.parent = element
            return this
        }

        fun child(builder: XmlElementBuilder): XmlElementBuilder {
            return child(builder.build())
        }

        fun build(): TagXmlElement {
            return element
        }

    }

    companion object {
        fun findChildForName(
            tagElement: TagXmlElement?,
            name: String,
            defaultValue: TagXmlElement?
        ): TagXmlElement? {
            if (tagElement == null) return defaultValue
            for (child in tagElement.children) {
                if (name == child.name) return child
            }
            return defaultValue
        }

        private fun getElementsByTagName(
            tagElement: TagXmlElement,
            name: String,
            list: MutableList<TagXmlElement>
        ) {
            if (name == tagElement.name) list.add(tagElement)
            for (child in tagElement.children) {
                getElementsByTagName(child, name, list)
            }
        }

        fun newElement(name: String): XmlElementBuilder {
            return XmlElementBuilder(name)
        }
    }
}