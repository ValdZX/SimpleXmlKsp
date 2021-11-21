package ua.vald_zx.simplexml.ksp.test.benchmark

import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.test.custompackage.TestSerializersEnrolment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class KspBenchmarkTest {

    init {
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

    private inline fun <reified G : Any> runBenchmark(
        makeGObj: () -> G,
        assertGObj: (G, G) -> Unit
    ) {
        val (gWriteTime, gReadTime) = runGBenchmark(makeGObj, assertGObj)
        println(G::class.simpleName)
        println("\t Generated: \n\t\tRead $gReadTime \n\t\tWrite $gWriteTime")
    }

    @Test
    fun PathAttribute_benchmark() = runBenchmark(
        makeGObj = {
            PathAttributeG().apply { attr = "PathAttribute" }
        },
        assertGObj = { bean, deserializedBean ->
            assertEquals(bean.attr, deserializedBean.attr)
        }
    )

    @Test
    fun RequiredField_benchmark() = runBenchmark(
        makeGObj = {
            RequiredFieldG().apply { tag = "RequiredField" }
        },
        assertGObj = { bean, deserializedBean ->
            assertEquals(bean.tag, deserializedBean.tag)
        }
    )

    @Test
    fun NullableFieldMutableListOfStrings_benchmark() = runBenchmark(
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
    fun FieldAndroidStringResources_benchmark() = runBenchmark(
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
    fun MultipleConvertersConstructorField_benchmark() = runBenchmark(
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