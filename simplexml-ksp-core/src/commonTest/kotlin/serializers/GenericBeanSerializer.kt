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
        val typeArg1 = genericTypeList[0].type
        val arguments1 = typeArg1?.arguments.orEmpty()
        val typeArg2 = genericTypeList[1].type
        val arguments2 = typeArg2?.arguments.orEmpty()
        val t1Serializer = GlobalSerializersLibrary.findSerializers(typeArg1?.classifier as KClass<Any>)
        val t2Serializer = GlobalSerializersLibrary.findSerializers(typeArg2?.classifier as KClass<Any>)
        tagFather.apply {
            val somObject1 = obj.somObject1 ?: throw SerializeException(
                """field somObject1 value is
          required"""
            )
            t1Serializer.buildXml(this, "somObject1", somObject1, arguments1)
            val somObject2 = obj.somObject2 ?: throw SerializeException(
                """field somObject2 value is
          required"""
            )
            t2Serializer.buildXml(this, "somObject2", somObject2, arguments2)
        }
    }

    override fun readData(element: XmlElement?, genericTypeList: List<KTypeProjection>):
            GenericBean<Any, Any> {
        val typeArg1 = genericTypeList[0].type
        val arguments1 = typeArg1?.arguments.orEmpty()
        val typeArg2 = genericTypeList[1].type
        val arguments2 = typeArg2?.arguments.orEmpty()
        val t1Serializer = GlobalSerializersLibrary.findSerializers(typeArg1?.classifier as KClass<*>)
        val t2Serializer = GlobalSerializersLibrary.findSerializers(typeArg2?.classifier as KClass<*>)
        element as TagXmlElement?
        val layer0Tag0 = element?.get("somObject1")
        val layer0Tag1 = element?.get("somObject2")
        return GenericBean<Any, Any>(
        ).apply {
            somObject1 = t1Serializer.readData(
                layer0Tag0 ?: throw DeserializeException("""field somObject1 value is required"""),
                arguments1
            )
            somObject2 = t2Serializer.readData(
                layer0Tag1 ?: throw DeserializeException("""field somObject2 value is required"""),
                arguments2
            )
        }
    }
}
