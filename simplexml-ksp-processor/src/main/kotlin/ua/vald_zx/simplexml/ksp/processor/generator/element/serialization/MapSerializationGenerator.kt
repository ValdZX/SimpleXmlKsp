package ua.vald_zx.simplexml.ksp.processor.generator.element.serialization

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field
import ua.vald_zx.simplexml.ksp.processor.generator.FieldSerializer
import ua.vald_zx.simplexml.ksp.processor.generator.renderChildren

class MapSerializationGenerator(private val field: Field.Map) : ElementSerializationGenerator {

    private var fieldSerializer: FieldSerializer? = null
    private lateinit var serializersMap: Map<Field, FieldSerializer>
    private var keySerializerName: String? = null
    private var keyGenericTypesVariableName: String? = null
    private var valueSerializerVariableName: String? = null
    private var valueGenericTypesVariableName: String? = null

    override fun render(
        funBuilder: FunSpec.Builder,
        fieldSerializer: FieldSerializer?,
        serializersMap: Map<Field, FieldSerializer>
    ) {
        keySerializerName = fieldSerializer?.firstValSerializer?.serializerVariableName
        keyGenericTypesVariableName = fieldSerializer?.firstValSerializer?.genericTypesVariableName
        valueSerializerVariableName = fieldSerializer?.secondValSerializer?.serializerVariableName
        valueGenericTypesVariableName = fieldSerializer?.secondValSerializer?.genericTypesVariableName
        this.fieldSerializer = fieldSerializer
        this.serializersMap = serializersMap
        if (field.isInline) {
            if (field.isNullable) {
                if (field.children.isNotEmpty()) {
                    throw error("Inline Map is have not children")
                } else {
                    funBuilder.nullableValueInline()
                }
            } else {
                if (field.children.isNotEmpty()) {
                    funBuilder.valueWithChildInline()
                } else {
                    funBuilder.valueInline()
                }
            }
        } else {
            if (field.isNullable) {
                if (field.children.isNotEmpty()) {
                    funBuilder.nullableValueWithChild()
                } else {
                    funBuilder.nullableValue()
                }
            } else {
                if (field.children.isNotEmpty()) {
                    funBuilder.valueWithChild()
                } else {
                    funBuilder.value()
                }
            }
        }
    }

    private fun FunSpec.Builder.value() {
        beginControlFlow("tag(\"${field.tagName}\") {")
        printMapForeach("obj.${field.fieldName}")
        endControlFlow()
    }

    private fun FunSpec.Builder.valueInline() {
        printMapForeach("obj.${field.fieldName}")
    }

    private fun FunSpec.Builder.valueWithChild() {
        beginControlFlow("tag(\"${field.tagName}\") {")
        renderChildren(field.children, serializersMap)
        printMapForeach("obj.${field.fieldName}")
        endControlFlow()
    }

    private fun FunSpec.Builder.valueWithChildInline() {
        printMapForeach("obj.${field.fieldName}")
    }

    private fun FunSpec.Builder.nullableValue() {
        beginControlFlow("obj.${field.fieldName}?.let")
        addStatement("map ->")
        beginControlFlow("tag(\"${field.tagName}\") {")
        printMapForeach("map")
        endControlFlow()
        endControlFlow()
    }

    private fun FunSpec.Builder.nullableValueWithChild() {
        beginControlFlow("obj.${field.fieldName}?.let")
        addStatement("map ->")
        beginControlFlow("tag(\"${field.tagName}\") {")
        renderChildren(field.children, serializersMap)
        printMapForeach("map")
        endControlFlow()
        endControlFlow()
    }

    private fun FunSpec.Builder.nullableValueInline() {
        beginControlFlow("obj.${field.fieldName}?.forEach")
        printMapForeach("it")
        endControlFlow()
    }

    private fun FunSpec.Builder.printMapForeach(objectName: String) {
        beginControlFlow("$objectName.forEach")
        addStatement("(key, value) ->")
        addStatement("$keySerializerName.buildXml(this, \"${field.keyName}\", key${keyGenericTypesVariableName.orEmpty()})")
        addStatement("$valueSerializerVariableName.buildXml(this, \"${field.entryName}\", value${valueGenericTypesVariableName.orEmpty()})")
        endControlFlow()
    }
}