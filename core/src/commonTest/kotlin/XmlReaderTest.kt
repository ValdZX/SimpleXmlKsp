import ua.vald_zx.simplexml.ksp.xml.XmlReader
import ua.vald_zx.simplexml.ksp.xml.utils.Escaping.unescapeXml
import ua.vald_zx.simplexml.ksp.xml.utils.InputStreamReader
import ua.vald_zx.simplexml.ksp.xml.utils.Trimming
import ua.vald_zx.simplexml.ksp.xml.utils.UnEscape
import kotlin.test.Test
import kotlin.test.assertTrue

class XmlReaderTest {

    @Test
    fun toXmlDomTest() {
        val input = InputStreamReader("<root><body>Nani?</body><body>Hani!</body></root>")
        val element = XmlReader.toXmlDom(input, Trimming.NativeTrimmer(), object : UnEscape {
            override fun unescape(input: String) = input.unescapeXml()
        })
        assertTrue { element != null }
        assertTrue { element?.children?.size == 2 }
        assertTrue { element?.children?.get(0)?.text == "Nani?" }
        assertTrue { element?.children?.get(1)?.text == "Hani!" }
    }
}