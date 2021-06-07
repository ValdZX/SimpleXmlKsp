import ua.vald_zx.simplexml.ksp.tag
import kotlin.test.Test
import kotlin.test.assertEquals

class XmlBuilderTest {

    private val prettyXml = """
<Auth>
    <UserId>1234</UserId>
    <Auth>
        <Password>qwerty</Password>
        <Auth>
            <Device>Android</Device>
        </Auth>
    </Auth>
</Auth>
    """.trimIndent()
    private val lineXml = "<Auth><UserId>1234</UserId><Auth lang=\"ru\"><Password>qwerty</Password><Auth><Device>Android</Device></Auth></Auth></Auth>"

    @Test
    fun buildPrettyXml() {
        val xmlString = tag("Auth", pretty = true) {
            tag("UserId", "1234")
            tag("Auth") {
                tag("Password", "qwerty")
                tag("Auth") {
                    tag("Device", "Android")
                }
            }
        }.render()
        assertEquals(xmlString, prettyXml)
    }
    @Test
    fun buildLineXml() {
        val xmlString = tag("Auth") {
            tag("UserId", "1234")
            tag("Auth") {
                attr("lang", "ru")
                tag("Password", "qwerty")
                tag("Auth") {
                    tag("Device", "Android")
                }
            }
        }.render()
        assertEquals(xmlString, lineXml)
    }
}