package ua.vald_zx.simplexml.ksp.processor.generator.element.serialization

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field
import ua.vald_zx.simplexml.ksp.processor.generator.FieldSerializer
import ua.vald_zx.simplexml.ksp.processor.generator.renderChildren

class MapSerializationGenerator(private val field: Field.Map) : ElementSerializationGenerator {

    private var fieldSerializer: FieldSerializer? = null
    private lateinit var serializersMap: Map<Field, FieldSerializer>
    private var keySerializerName: String? = null
    private var keyGenericTypesVariableName: String = ""
    private var valueSerializerVariableName: String? = null
    private var valueGenericTypesVariableName: String = ""

    override fun render(
        funBuilder: FunSpec.Builder,
        fieldSerializer: FieldSerializer?,
        serializersMap: Map<Field, FieldSerializer>
    ) {
        keySerializerName = fieldSerializer?.firstValSerializer?.serializerVariableName
        keyGenericTypesVariableName =
            fieldSerializer?.firstValSerializer?.genericTypesVariableName?.let { ", $it" }.orEmpty()
        valueSerializerVariableName = fieldSerializer?.secondValSerializer?.serializerVariableName
        valueGenericTypesVariableName =
            fieldSerializer?.secondValSerializer?.genericTypesVariableName?.let { ", $it" }.orEmpty()
        this.fieldSerializer = fieldSerializer
        this.serializersMap = serializersMap

        if (field.isNullable) {
            funBuilder.nullableValue()
        } else {
            funBuilder.value()
        }
    }

    private fun FunSpec.Builder.value() {
        renderMap("obj.${field.fieldName}")
    }

    private fun FunSpec.Builder.nullableValue() {
        beginControlFlow("obj.${field.fieldName}?.let")
        addStatement("map ->")
        renderMap("map")
        endControlFlow()
    }

    private fun FunSpec.Builder.renderMap(objectName: String) {
        if (!field.isInline) {
            beginControlFlow("tag(\"${field.tagName}\") {")
            renderChildren(field.children, serializersMap)
        }
        beginControlFlow("$objectName.mapNotNull { (key, value) -> key?.let { value?.let { key to value } } }.forEach")
        addStatement("(key, value) ->")
        if (field.isAttributeKey) {
            printAttributeMapForeach()
        } else {
            printMapForeach()
        }
        endControlFlow()
        if (!field.isInline) {
            endControlFlow()
        }
    }

    private fun FunSpec.Builder.printMapForeach() {
        addStatement("$keySerializerName.buildXml(this, \"${field.keyName}\", key${keyGenericTypesVariableName})")
        addStatement("$valueSerializerVariableName.buildXml(this, \"${field.entryName}\", value${valueGenericTypesVariableName})")
    }

    private fun FunSpec.Builder.printAttributeMapForeach() {
        beginControlFlow("$valueSerializerVariableName.buildXml(this, \"${field.entryName}\", value${valueGenericTypesVariableName})")
        val serializeCall = "$keySerializerName.serialize(key${keyGenericTypesVariableName})"
        addStatement("attr(\"${field.keyName}\", $serializeCall)")
        endControlFlow()
    }
}