package ua.vald_zx.simplexml.ksp.processor.generator.element.serialization

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field
import ua.vald_zx.simplexml.ksp.processor.generator.FieldSerializer
import ua.vald_zx.simplexml.ksp.processor.generator.renderChildren

class MapSerializationGenerator(private val field: Field.Map) : ElementSerializationGenerator {

    private var fieldSerializer: FieldSerializer? = null
    private lateinit var serializersMap: Map<Field, FieldSerializer>

    override fun render(
        funBuilder: FunSpec.Builder,
        fieldSerializer: FieldSerializer?,
        serializersMap: Map<Field, FieldSerializer>
    ) {
        this.fieldSerializer = fieldSerializer
        this.serializersMap = serializersMap
        funBuilder.map()
    }

    private fun FunSpec.Builder.map() {
        if (field.isNullable) {
            mapNullableValue(fieldSerializer, field)
        } else {
            val keySerializerName = fieldSerializer?.serializerVariableName
            val keyGenericTypesVariableName = fieldSerializer?.genericTypesVariableName
            val valueSerializerVariableName = fieldSerializer?.valueSerializerVariableName
            val valueGenericTypesVariableName = fieldSerializer?.valueGenericTypesVariableName
            if (field.children.isNotEmpty()) {
                if (!field.isInline) {
                    beginControlFlow("tag(\"${field.tagName}\") {")
                    renderChildren(field.children, serializersMap)
                }
                printMapForeach(
                    field,
                    keySerializerName,
                    keyGenericTypesVariableName,
                    valueSerializerVariableName,
                    valueGenericTypesVariableName,
                    "obj.${field.fieldName}"
                )
                if (!field.isInline) {
                    endControlFlow()
                }
            } else {
                if (!field.isInline) {
                    beginControlFlow("tag(\"${field.tagName}\") {")
                }
                printMapForeach(
                    field,
                    keySerializerName,
                    keyGenericTypesVariableName,
                    valueSerializerVariableName,
                    valueGenericTypesVariableName,
                    "obj.${field.fieldName}"
                )
                if (!field.isInline) {
                    endControlFlow()
                }
            }
        }
    }

    private fun FunSpec.Builder.mapNullableValue(fieldSerializer: FieldSerializer?, element: Field.Map) {
        val keySerializerName = fieldSerializer?.serializerVariableName
        val keyGenericTypesVariableName = fieldSerializer?.genericTypesVariableName
        val valueSerializerVariableName = fieldSerializer?.valueSerializerVariableName
        val valueGenericTypesVariableName = fieldSerializer?.valueGenericTypesVariableName

        if (element.isInline) {
            beginControlFlow("obj.${element.fieldName}?.forEach")
            printMapForeach(
                element,
                keySerializerName,
                keyGenericTypesVariableName,
                valueSerializerVariableName,
                valueGenericTypesVariableName,
                "it"
            )
            endControlFlow()
        } else {
            beginControlFlow("obj.${element.fieldName}?.let")
            addStatement("map ->")
            beginControlFlow("tag(\"${element.tagName}\") {")
            printMapForeach(
                element,
                keySerializerName,
                keyGenericTypesVariableName,
                valueSerializerVariableName,
                valueGenericTypesVariableName,
                "map"
            )
            endControlFlow()
            endControlFlow()
        }
    }

    private fun FunSpec.Builder.printMapForeach(
        element: Field.Map,
        keySerializerName: String?,
        keyArgumentsFunArgument: String?,
        valueSerializerName: String?,
        valueArgumentsFunArgument: String?,
        fieldName: String
    ) {
        beginControlFlow("$fieldName.forEach")
        addStatement("(key, value) ->")
        addStatement("$keySerializerName.buildXml(this, \"${element.keyName}\", key${keyArgumentsFunArgument.orEmpty()})")
        addStatement("$valueSerializerName.buildXml(this, \"${element.entryName}\", value${valueArgumentsFunArgument.orEmpty()})")
        endControlFlow()
    }
}