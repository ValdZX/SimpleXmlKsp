package ua.vald_zx.simplexml.ksp.xml

import ua.vald_zx.simplexml.ksp.xml.model.XmlElement
import ua.vald_zx.simplexml.ksp.xml.parsing.DomBuilder
import ua.vald_zx.simplexml.ksp.xml.utils.Escaping.unescapeXml
import ua.vald_zx.simplexml.ksp.xml.utils.InputStreamReader
import ua.vald_zx.simplexml.ksp.xml.utils.Trim
import ua.vald_zx.simplexml.ksp.xml.utils.Trimming
import ua.vald_zx.simplexml.ksp.xml.utils.UnEscape

object XmlReader {
    fun String.readXml(): XmlElement? {
        val input = InputStreamReader(this)
        return toXmlDom(input, Trimming.NativeTrimmer(), object : UnEscape {
            override fun unescape(input: String) = input.unescapeXml()
        })
    }

    internal fun toXmlDom(input: InputStreamReader, trimmer: Trim, escaper: UnEscape): XmlElement? {
        val p = DomBuilder()
        XmlStreamReader.toXmlStream(input, p, trimmer, escaper)
        return p.root
    }
}