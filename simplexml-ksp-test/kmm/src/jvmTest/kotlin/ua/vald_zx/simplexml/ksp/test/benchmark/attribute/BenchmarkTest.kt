package ua.vald_zx.simplexml.ksp.test.benchmark.attribute

import org.simpleframework.xml.Serializer
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.strategy.Strategy
import org.simpleframework.xml.strategy.TreeStrategy
import org.simpleframework.xml.strategy.Type
import org.simpleframework.xml.stream.NodeMap
import org.simpleframework.xml.transform.RegistryMatcher
import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.test.custompackage.TestSerializersEnrolment
import java.io.StringWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class BenchmarkTest {

    private var serializer: Serializer

    init {
        val matcher = RegistryMatcher()
        val treeStrategy: Strategy = object : TreeStrategy("", "") {
            override fun write(type: Type, value: Any, node: NodeMap<*>?, map: Map<*, *>?): Boolean {
                return false
            }
        }
        val strategy: Strategy = AnnotationStrategy(treeStrategy)
        serializer = Persister(strategy, matcher)
        TestSerializersEnrolment.enrol()
    }

    private inline fun <reified R : Any, reified G : Any> runBenchmark(
        makeRObj: () -> R,
        assertRObj: (R, R) -> Unit,
        makeGObj: () -> G,
        assertGObj: (G, G) -> Unit
    ) {
        val beanR = makeRObj()
        val deserializedBeanR: R
        val rTime = measureTime {
            repeat(1000) {
                val stringWriter = StringWriter()
                serializer.write(beanR, stringWriter)
                val xml = stringWriter.buffer.toString()
                serializer.read(R::class.java, xml)
            }
            val stringWriter = StringWriter()
            serializer.write(beanR, stringWriter)
            val xml = stringWriter.buffer.toString()
            deserializedBeanR = serializer.read(R::class.java, xml)
        }
        assertRObj(beanR, deserializedBeanR)
        val beanG = makeGObj()
        val deserializedBeanG: G
        val gTime = measureTime {
            repeat(1000) {
                val xml = SimpleXml.serialize(beanG)
                SimpleXml.deserialize<G>(xml)
            }
            val xml = SimpleXml.serialize(beanG)
            deserializedBeanG = SimpleXml.deserialize(xml)
        }
        assertGObj(beanG, deserializedBeanG)
        println(R::class.simpleName)
        println("\t Reflection: $rTime")
        println("\t Generated:  $gTime")
    }

    @Test
    fun `PathAttribute benchmark`() = runBenchmark(
        makeRObj = {
            PathAttributeR().apply { attr = "PathAttribute" }
        },
        assertRObj = { bean, deserializedBean ->
            assertEquals(bean.attr, deserializedBean.attr)
        },
        makeGObj = {
            PathAttributeG().apply { attr = "PathAttribute" }
        },
        assertGObj = { bean, deserializedBean ->
            assertEquals(bean.attr, deserializedBean.attr)
        }
    )

    @Test
    fun `RequiredField benchmark`() = runBenchmark(
        makeRObj = {
            RequiredFieldR().apply { tag = "RequiredField" }
        },
        assertRObj = { bean, deserializedBean ->
            assertEquals(bean.tag, deserializedBean.tag)
        },
        makeGObj = {
            RequiredFieldG().apply { tag = "RequiredField" }
        },
        assertGObj = { bean, deserializedBean ->
            assertEquals(bean.tag, deserializedBean.tag)
        }
    )

    @Test
    fun `NullableFieldMutableListOfStrings benchmark`() = runBenchmark(
        makeRObj = {
            NullableFieldMutableListOfStringsR().apply {
                list = mutableListOf("Value1", "Value2", "Value3")
            }
        },
        assertRObj = { bean, deserializedBean ->
            assertEquals(bean.list, deserializedBean.list)
        },
        makeGObj = {
            NullableFieldMutableListOfStringsG().apply {
                list = mutableListOf("Value1", "Value2", "Value3")
            }
        },
        assertGObj = { bean, deserializedBean ->
            assertEquals(bean.list, deserializedBean.list)
        }
    )

    @Test
    fun `FieldAndroidStringResources benchmark`() = runBenchmark(
        makeRObj = {
            FieldAndroidStringResourcesR().apply {
                resources = mutableMapOf(
                    "appName" to "The best app",
                    "greetings" to "Hello!"
                )
            }
        },
        assertRObj = { bean, deserializedBean ->
            assertEquals(bean.resources, deserializedBean.resources)
        },
        makeGObj = {
            FieldAndroidStringResourcesG().apply {
                resources = mutableMapOf(
                    "appName" to "The best app",
                    "greetings" to "Hello!"
                )
            }
        },
        assertGObj = { bean, deserializedBean ->
            assertEquals(bean.resources, deserializedBean.resources)
        }
    )

    @Test
    fun `MultipleConvertersConstructorField benchmark`() = runBenchmark(
        makeRObj = {
            MultipleConvertersConstructorFieldR().apply {
                tag1 = "Value1"
                tag2 = 34.0
            }
        },
        assertRObj = { bean, deserializedBean ->
            assertEquals(bean.tag1, deserializedBean.tag1)
            assertEquals(bean.tag2, deserializedBean.tag2)
        },
        makeGObj = {
            MultipleConvertersConstructorFieldG().apply {
                tag1 = "Value1"
                tag2 = 34.0
            }
        },
        assertGObj = { bean, deserializedBean ->
            assertEquals(bean.tag1, deserializedBean.tag1)
            assertEquals(bean.tag2, deserializedBean.tag2)
        }
    )
}