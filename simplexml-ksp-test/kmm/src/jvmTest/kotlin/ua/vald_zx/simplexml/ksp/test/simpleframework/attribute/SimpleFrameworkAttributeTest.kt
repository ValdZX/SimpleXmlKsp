package ua.vald_zx.simplexml.ksp.test.simpleframework.attribute

import org.simpleframework.xml.Serializer
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.ElementException
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.strategy.Strategy
import org.simpleframework.xml.strategy.TreeStrategy
import org.simpleframework.xml.strategy.Type
import org.simpleframework.xml.stream.NodeMap
import org.simpleframework.xml.transform.RegistryMatcher
import java.io.StringWriter
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SimpleFrameworkAttributeTest {

    private lateinit var serializer: Serializer

    @BeforeTest
    fun init() {
        val matcher = RegistryMatcher()
        val treeStrategy: Strategy = object : TreeStrategy("", "") {
            override fun write(type: Type, value: Any, node: NodeMap<*>?, map: Map<*, *>?): Boolean {
                return false
            }
        }
        val strategy: Strategy = AnnotationStrategy(treeStrategy)
        serializer = Persister(strategy, matcher)
    }

    @Test
    fun `RootAttribute serialize deserialize test`() {
        val bean = RootAttribute("RootAttribute")
        val stringWriter = StringWriter()
        serializer.write(bean, stringWriter)
        val xml = stringWriter.buffer.toString()
        println(xml)
        val deserializedBean: RootAttribute = serializer.read(RootAttribute::class.java, xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    fun `PathAttribute serialize deserialize test`() {
        val bean = PathAttribute()
        bean.attr = "PathAttribute"
        val stringWriter = StringWriter()
        serializer.write(bean, stringWriter)
        val xml = stringWriter.buffer.toString()
        println(xml)
        val deserializedBean: PathAttribute = serializer.read(PathAttribute::class.java, xml)
        assertEquals(bean.attr, deserializedBean.attr)
    }

    @Test
    fun `TagAttribute serialize deserialize test`() {
        assertFailsWith<ElementException> {
            val bean = TagAttribute("TagAttributeATTR", "TagAttributeTAG")
            val stringWriter = StringWriter()
            serializer.write(bean, stringWriter)
            val xml = stringWriter.buffer.toString()
            println(xml)
            val deserializedBean: TagAttribute = serializer.read(TagAttribute::class.java, xml)
            assertEquals(bean, deserializedBean)
        }
    }

    @Test
    fun `ListAttribute serialize deserialize test`() {
        assertFailsWith<ElementException> {
            val bean = ListAttribute()
            bean.attr = "ListAttribute"
            bean.tag = listOf("Value1", "Value2", "Value3")
            val stringWriter = StringWriter()
            serializer.write(bean, stringWriter)
            val xml = stringWriter.buffer.toString()
            println(xml)
            val deserializedBean: ListAttribute = serializer.read(ListAttribute::class.java, xml)
            assertEquals(bean.attr, deserializedBean.attr)
            assertEquals(bean.tag, deserializedBean.tag)
        }
    }

    @Test
    fun `NullableTagAttribute serialize deserialize test`() {
        assertFailsWith<ElementException> {
            val bean = NullableTagAttribute("NullableTagAttributeATTR", "NullableTagAttributeTAG")
            val stringWriter = StringWriter()
            serializer.write(bean, stringWriter)
            val xml = stringWriter.buffer.toString()
            println(xml)
            val deserializedBean: NullableTagAttribute = serializer.read(NullableTagAttribute::class.java, xml)
            assertEquals(bean, deserializedBean)
        }
    }

    @Test
    fun `NullableTagAttribute with empty tag serialize deserialize test`() {
        assertFailsWith<ElementException> {
            val bean = NullableTagAttribute("NullableTagAttributeATTR")
            val stringWriter = StringWriter()
            serializer.write(bean, stringWriter)
            val xml = stringWriter.buffer.toString()
            println(xml)
            val deserializedBean: NullableTagAttribute = serializer.read(NullableTagAttribute::class.java, xml)
            assertEquals(bean.attr, deserializedBean.attr)
            assertEquals(deserializedBean.tag, "")
        }
    }
}