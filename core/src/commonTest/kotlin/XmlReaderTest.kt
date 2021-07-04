import ua.vald_zx.simplexml.ksp.xml.XmlReader
import ua.vald_zx.simplexml.ksp.xml.utils.Escaping.unescapeXml
import ua.vald_zx.simplexml.ksp.xml.utils.StringReader
import ua.vald_zx.simplexml.ksp.xml.utils.Trimming
import ua.vald_zx.simplexml.ksp.xml.utils.UnEscape
import kotlin.test.Test
import kotlin.test.assertTrue

class XmlReaderTest {

    @Test
    fun toXmlDomTest() {
        val input = StringReader("<root><body>Nani?</body><body>Hani!</body><woody>Money!</woody></root>")
        val element = XmlReader.toXmlDom(input, Trimming.NativeTrimmer(), object : UnEscape {
            override fun unescape(input: String) = input.unescapeXml()
        })
        assertTrue { element != null }
        assertTrue { element?.children?.size == 3 }
        assertTrue { element?.children?.get(0)?.text == "Nani?" }
        assertTrue { element?.children?.get(1)?.text == "Hani!" }
        assertTrue { element?.children?.get(2)?.text == "Money!" }
    }

    @Test
    fun readAllTest() {
        val input = StringReader("<root><body>Nani?</body><body>Hani!</body><woody>Hani!</woody></root>")
        val element = XmlReader.toXmlDom(input, Trimming.NativeTrimmer(), object : UnEscape {
            override fun unescape(input: String) = input.unescapeXml()
        })
        val bodyList = element?.getAll("body").orEmpty()
        assertTrue { bodyList.size == 2 }
        assertTrue { bodyList[0].text == "Nani?" }
        assertTrue { bodyList[1].text == "Hani!" }
    }
}