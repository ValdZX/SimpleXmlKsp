package ua.vald_zx.simplexml.ksp.test.benchmark

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
import kotlin.time.Duration
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


    private inline fun <reified G : Any> runGBenchmark(
        makeGObj: () -> G,
        assertGObj: (G, G) -> Unit
    ): Pair<Duration, Duration> {
        val beanG = makeGObj()
        val deserializedBeanG: G
        val gXml: String
        val gWriteTime = measureTime {
            repeat(1000) {
                SimpleXml.serialize(beanG)
            }
            gXml = SimpleXml.serialize(beanG)
        }
        val gReadTime = measureTime {
            repeat(1000) {
                SimpleXml.deserialize<G>(gXml)
            }
            deserializedBeanG = SimpleXml.deserialize(gXml)
        }
        assertGObj(beanG, deserializedBeanG)
        return gWriteTime to gReadTime
    }

    private inline fun <reified R : Any> runRBenchmark(
        makeRObj: () -> R,
        assertRObj: (R, R) -> Unit
    ): Pair<Duration, Duration> {
        val beanR = makeRObj()
        val deserializedBeanR: R
        val rXml: String
        val rWriteTime = measureTime {
            repeat(1000) {
                val stringWriter = StringWriter()
                serializer.write(beanR, stringWriter)
                stringWriter.buffer.toString()
            }
            val stringWriter = StringWriter()
            serializer.write(beanR, stringWriter)
            rXml = stringWriter.buffer.toString()
        }
        val rReadTime = measureTime {
            repeat(1000) {
                serializer.read(R::class.java, rXml)
            }
            deserializedBeanR = serializer.read(R::class.java, rXml)
        }
        assertRObj(beanR, deserializedBeanR)
        return rWriteTime to rReadTime
    }

    private inline fun <reified R : Any, reified G : Any> runBenchmark(
        makeRObj: () -> R,
        assertRObj: (R, R) -> Unit,
        makeGObj: () -> G,
        assertGObj: (G, G) -> Unit
    ) {
        val (rWriteTime, rReadTime) = runRBenchmark(makeRObj, assertRObj)
        val (gWriteTime, gReadTime) = runGBenchmark(makeGObj, assertGObj)
        println(R::class.simpleName)
        println("\t Reflection: \n\t\tRead $rReadTime \n\t\tWrite $rWriteTime")
        println("\t Generated: \n\t\tRead $gReadTime \n\t\tWrite $gWriteTime")
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