package ua.vald_zx.simplexml.ksp.xml

import ua.vald_zx.simplexml.ksp.xml.model.TagXmlElement
import ua.vald_zx.simplexml.ksp.xml.parsing.DomBuilder
import ua.vald_zx.simplexml.ksp.xml.utils.Escaping.unescapeXml
import ua.vald_zx.simplexml.ksp.xml.utils.StringReader
import ua.vald_zx.simplexml.ksp.xml.utils.Trim
import ua.vald_zx.simplexml.ksp.xml.utils.Trimming
import ua.vald_zx.simplexml.ksp.xml.utils.UnEscape

object XmlReader {
    fun String.readXml(): TagXmlElement? {
        val input = StringReader(this)
        return toXmlDom(input, Trimming.NativeTrimmer(), object : UnEscape {
            override fun unescape(input: String) = input.unescapeXml()
        })
    }

    internal fun toXmlDom(input: StringReader, trimmer: Trim, escaper: UnEscape): TagXmlElement? {
        val p = DomBuilder()
        XmlStreamReader.toXmlStream(input, p, trimmer, escaper)
        return p.root
    }
}