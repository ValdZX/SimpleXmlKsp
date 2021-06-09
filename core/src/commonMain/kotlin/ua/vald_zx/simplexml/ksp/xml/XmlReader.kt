package ua.vald_zx.simplexml.ksp.xml

import ua.vald_zx.simplexml.ksp.xml.model.XmlElement
import ua.vald_zx.simplexml.ksp.xml.parsing.DomBuilder
import ua.vald_zx.simplexml.ksp.xml.utils.InputStreamReader
import ua.vald_zx.simplexml.ksp.xml.utils.Trim
import ua.vald_zx.simplexml.ksp.xml.utils.UnEscape

object XmlReader {
    fun toXmlDom(input: InputStreamReader, trimmer: Trim, escaper: UnEscape): XmlElement? {
        val p = DomBuilder()
        XmlStreamReader.toXmlStream(input, p, trimmer, escaper)
        return p.root
    }
}