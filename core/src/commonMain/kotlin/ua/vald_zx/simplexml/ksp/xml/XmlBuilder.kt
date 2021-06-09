package ua.vald_zx.simplexml.ksp.xml

import ua.vald_zx.simplexml.ksp.xml.utils.Escaping.unescapeHtml

@DslMarker
annotation class TagMarker

private const val TAB = "    "

data class Attribute(val name: String, val value: String)

@TagMarker
interface Tag {
    val name: String
    val attributes: MutableList<Attribute>

    fun attr(name: String, value: String) = attributes.add(Attribute(name.clean(), value.unescapeHtml()))

    fun render(margin: Int = 0): String

    fun renderStartTeg(isClosed: Boolean = false) = StringBuilder().apply {
        append("<$name")
        attributes.forEach { append(" ${it.name}=\"${it.value}\"") }
        if (isClosed) append("/>") else append(">")
    }
}

data class TagFather(
    override var name: String,
    override val attributes: MutableList<Attribute> = mutableListOf(),
    val tags: MutableList<Tag> = mutableListOf(),
    val pretty: Boolean = false
) : Tag {
    fun tag(name: String, block: TagFather.() -> Unit = {}) =
        tags.add(TagFather(name.clean(), pretty = pretty).apply(block))

    fun tag(name: String, value: String, block: TagValue.() -> Unit = {}) =
        tags.add(TagValue(name.clean(), value.unescapeHtml(), pretty = pretty).apply(block))

    override fun render(margin: Int): String = StringBuilder().apply {
        if (pretty) {
            appendLine(TAB.repeat(margin) + renderStartTeg(tags.isEmpty()))
        } else {
            append(renderStartTeg(tags.isEmpty()))
        }
        if (tags.isNotEmpty()) {
            if (pretty) {
                tags.forEach { appendLine(it.render(margin + 1)) }
                append(TAB.repeat(margin) + "</$name>")
            } else {
                tags.forEach { append(it.render(margin + 1)) }
                append("</$name>")
            }
        }
    }.toString()
}

data class TagValue(
    override var name: String,
    val value: String,
    override val attributes: MutableList<Attribute> = mutableListOf(),
    val pretty: Boolean = false
) : Tag {
    override fun render(margin: Int) = StringBuilder().apply {
        if (pretty) {
            append(TAB.repeat(margin) + renderStartTeg(value.isBlank()))
        } else {
            append(renderStartTeg(value.isBlank()))
        }
        if (value.isNotBlank()) {
            append(value)
            append("</$name>")
        }
    }.toString()
}

fun tag(name: String, pretty: Boolean = false, block: TagFather.() -> Unit) =
    TagFather(name.clean(), pretty = pretty).apply {
        block()
    }

private fun String.clean() = replace(Regex("[\\\\!\"#\$%&'()*+,/;<=>?@\\[\\]^`{|}~ ]"), "")
    .replace(Regex("^([-.])+"), "")