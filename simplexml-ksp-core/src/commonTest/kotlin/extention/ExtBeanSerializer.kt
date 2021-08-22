package extention


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

open class AbsBean<T1, T2> {
    @Element
    var somObject1: T1? = null

    @Element
    open var somObject2: T2? = null
}

open class ExtBean<T1, T2, T3> : AbsBean<T1, T2>() {
    @Element
    override var somObject2: T2? = null

    @Element
    var somObject3: T2? = null

    @Element
    var somObject4: T3? = null
}

public object AbsBeanSerializer : ObjectSerializer<AbsBean<*, *>>() {
    public override val rootName: String = "AbsBean"

    @Suppress("UNCHECKED_CAST")
    public override fun buildXml(
        tagFather: TagFather,
        obj: AbsBean<*, *>,
        genericTypeList: List<KTypeProjection>
    ): Unit {
        val t1Type = genericTypeList[0].type
        val t1Args = t1Type?.arguments.orEmpty()
        val t1Serializer = GlobalSerializersLibrary.findSerializers(t1Type?.classifier as KClass<Any>)
        val t2Type = genericTypeList[1].type
        val t2Args = t2Type?.arguments.orEmpty()
        val t2Serializer = GlobalSerializersLibrary.findSerializers(t2Type?.classifier as KClass<Any>)
        tagFather.apply {
            val somObject1 = obj.somObject1?: throw SerializeException("""field somObject1 value is
          required""")
            t1Serializer.buildXml(this, "somObject1", somObject1, t1Args)
            val somObject2 = obj.somObject2?: throw SerializeException("""field somObject2 value is
          required""")
            t2Serializer.buildXml(this, "somObject2", somObject2, t2Args)
        }
    }

    public override fun readData(element: XmlElement?, genericTypeList: List<KTypeProjection>):
            AbsBean<Any, Any> {
        val t1Type = genericTypeList[0].type
        val t1Args = t1Type?.arguments.orEmpty()
        val t1Serializer = GlobalSerializersLibrary.findSerializers(t1Type?.classifier as KClass<Any>)
        val t2Type = genericTypeList[1].type
        val t2Args = t2Type?.arguments.orEmpty()
        val t2Serializer = GlobalSerializersLibrary.findSerializers(t2Type?.classifier as KClass<Any>)
        element as TagXmlElement?
        val layer0Tag0 = element?.get("somObject1")
        val layer0Tag1 = element?.get("somObject2")
        return AbsBean<Any, Any>(
        ).apply {
            somObject1 = t1Serializer.readData(
                layer0Tag0 ?: throw DeserializeException("""field somObject1 value is required"""), t1Args
            )
            somObject2 = t2Serializer.readData(
                layer0Tag1 ?: throw DeserializeException("""field somObject2 value is required"""), t2Args
            )
        }
    }
}

public object ExtBeanSerializer : ObjectSerializer<ExtBean<*, *, *>>() {
    public override val rootName: String = "ExtBean"

    @Suppress("UNCHECKED_CAST")
    public override fun buildXml(
        tagFather: TagFather,
        obj: ExtBean<*, *, *>,
        genericTypeList: List<KTypeProjection>
    ): Unit {
        val t2Type = genericTypeList[1].type
        val t2Args = t2Type?.arguments.orEmpty()
        val t2Serializer = GlobalSerializersLibrary.findSerializers(t2Type?.classifier as KClass<Any>)
        val t3Type = genericTypeList[2].type
        val t3Args = t3Type?.arguments.orEmpty()
        val t3Serializer = GlobalSerializersLibrary.findSerializers(t3Type?.classifier as KClass<Any>)
        tagFather.apply {
            val somObject2 = obj.somObject2?: throw SerializeException("""field somObject2 value is
          required""")
            t2Serializer.buildXml(this, "somObject2", somObject2, t2Args)
            val somObject3 = obj.somObject3?: throw SerializeException("""field somObject3 value is
          required""")
            t2Serializer.buildXml(this, "somObject3", somObject3, t2Args)
            val somObject4 = obj.somObject4?: throw SerializeException("""field somObject4 value is
          required""")
            t3Serializer.buildXml(this, "somObject4", somObject4, t3Args)
        }
    }

    public override fun readData(element: XmlElement?, genericTypeList: List<KTypeProjection>):
            ExtBean<Any, Any, Any> {
        val t2Type = genericTypeList[1].type
        val t2Args = t2Type?.arguments.orEmpty()
        val t2Serializer = GlobalSerializersLibrary.findSerializers(t2Type?.classifier as KClass<Any>)
        val t3Type = genericTypeList[2].type
        val t3Args = t3Type?.arguments.orEmpty()
        val t3Serializer = GlobalSerializersLibrary.findSerializers(t3Type?.classifier as KClass<Any>)
        element as TagXmlElement?
        val layer0Tag0 = element?.get("somObject2")
        val layer0Tag1 = element?.get("somObject3")
        val layer0Tag2 = element?.get("somObject4")
        return ExtBean<Any, Any, Any>(
        ).apply {
            somObject2 = t2Serializer.readData(
                layer0Tag0 ?: throw DeserializeException("""field somObject2 value is required"""), t2Args
            )
            somObject3 = t2Serializer.readData(
                layer0Tag1 ?: throw DeserializeException("""field somObject3 value is required"""), t2Args
            )
            somObject4 = t3Serializer.readData(
                layer0Tag2 ?: throw DeserializeException("""field somObject4 value is required"""), t3Args
            )
        }
    }
}