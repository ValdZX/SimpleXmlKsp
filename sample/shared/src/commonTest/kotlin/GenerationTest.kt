import ua.vald_zx.simplexml.ksp.Serializer
import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.sample.beans.Auth
import ua.vald_zx.simplexml.ksp.sample.beans.XmlBean
import ua.vald_zx.simplexml.ksp.sample.custompackage.SampleModuleInitializer
import ua.vald_zx.simplexml.ksp.xml.XmlReader.readXml
import ua.vald_zx.simplexml.ksp.xml.error.InvalidXml
import ua.vald_zx.simplexml.ksp.xml.tag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GenerationTest {

    @Test
    fun authTest() {
        SampleModuleInitializer.init()
        val auth = Auth("Vald_ZX", "Kharkiv", "Linkoln", "Android", "18.06.2021 1:03", "EN")
        auth.legend2 = "Legend2"
        auth.legend4 = "Legend4"
        auth.phones = listOf("+380502808980", "+380952808982", "+380572808984")
        auth.addresses = listOf("Klochkovskaya, 244", "OJarosha, 51", "Svobody, 8")
        val xml = SimpleXml.serialize(auth)
        println(xml)
        val deserializedAuth = SimpleXml.deserialize<Auth>(xml)
        assertEquals(auth, deserializedAuth)
        assertEquals(auth.legend2, deserializedAuth.legend2)
        assertEquals(auth.legend4, deserializedAuth.legend4)
        assertEquals(auth.phones, deserializedAuth.phones)
        assertEquals(auth.addresses, deserializedAuth.addresses)
    }

    @Test
    fun xmlBeanTest() {
        SampleModuleInitializer.init()
        val bean = XmlBean(
            layer0TagString0 = "1",
            layer0TagString1 = "2",
            layer1Path0TagString0 = "3",
            layer1Path0TagString1 = "4",
            layer2Path0TagString0 = "5",
            layer2Path0TagString1 = "6",
            layer3Path0TagString0 = "7",
            layer3Path0TagString1 = "8",
            layer3Path1TagString1 = "9",
            layer3Path1TagString0 = "10",
            layer2Path1TagString1 = "11",
            layer2Path1TagString0 = "12",
            layer1Path1TagString1 = "13",
            layer1Path1TagString0 = "14",
            layer0TagString4 = "15",
            layer0TagString3 = "16",
            layer0Path2AttributeString0 = "17",
            layer0Path2AttributeString1 = "18",
            layer0Path3AttributeString0 = "19",
            layer0Path3AttributeString1 = "20",
            layer1Path4AttributeString0 = "21",
            layer1Path4AttributeString1 = "22",
            layer1Path5AttributeString0 = "23",
            layer1Path5AttributeString1 = "24",
            Layer0Path6Tag = "25",
            layer0Path6AttributeString0 = "26",
            layer0Path6AttributeString1 = "27",
        )
        val xml = SimpleXml.serialize(bean)
        val deserializedBean = SimpleXml.deserialize<XmlBean>(xml)
        assertEquals(bean, deserializedBean)
    }
}