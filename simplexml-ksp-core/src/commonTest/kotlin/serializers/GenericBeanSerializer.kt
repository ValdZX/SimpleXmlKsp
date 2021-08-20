package serializers


import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.GlobalSerializersLibrary
import ua.vald_zx.simplexml.ksp.exception.DeserializeException
import ua.vald_zx.simplexml.ksp.exception.SerializeException
import ua.vald_zx.simplexml.ksp.serializers.ObjectSerializer
import ua.vald_zx.simplexml.ksp.xml.TagFather
import ua.vald_zx.simplexml.ksp.xml.model.TagXmlElement
import ua.vald_zx.simplexml.ksp.xml.model.XmlElement
import kotlin.reflect.KClass
import kotlin.reflect.KTypeProjection

open class GenericBean<T1, T2> {
    @Element
    var somObject1: T1? = null

    @Element
    var somObject2: T2? = null
}

object GenericBeanSerializer : ObjectSerializer<GenericBean<*, *>>() {
    public override val rootName: String = "GenericBean"

    @Suppress("UNCHECKED_CAST")
    override fun buildXml(
        tagFather: TagFather,
        obj: GenericBean<*, *>,
        genericTypeList: List<KTypeProjection>
    ) {
        val t1Serializer = GlobalSerializersLibrary.findSerializers(genericTypeList[0].type?.classifier as KClass<Any>)
        val t2Serializer = GlobalSerializersLibrary.findSerializers(genericTypeList[1].type?.classifier as KClass<Any>)
        tagFather.apply {
            val somObject1 = obj.somObject1 ?: throw SerializeException(
                """field somObject1 value is
          required"""
            )
            t1Serializer.buildXml(this, "somObject1", somObject1)
            val somObject2 = obj.somObject2 ?: throw SerializeException(
                """field somObject2 value is
          required"""
            )
            t2Serializer.buildXml(this, "somObject2", somObject2)
        }
    }

    override fun readData(element: XmlElement?, genericTypeList: List<KTypeProjection>):
            GenericBean<Any, Any> {
        val t1Serializer = GlobalSerializersLibrary.findSerializers(genericTypeList[0].type?.classifier as KClass<*>)
        val t2Serializer = GlobalSerializersLibrary.findSerializers(genericTypeList[1].type?.classifier as KClass<*>)
        element as TagXmlElement?
        val layer0Tag0 = element?.get("somObject1")
        val layer0Tag1 = element?.get("somObject2")
        return GenericBean<Any, Any>(
        ).apply {
            somObject1 = t1Serializer.readData(
                layer0Tag0 ?: throw DeserializeException("""field somObject1 value is required""")
            )
            somObject2 = t2Serializer.readData(
                layer0Tag1 ?: throw DeserializeException("""field somObject2 value is required""")
            )
        }
    }
}
