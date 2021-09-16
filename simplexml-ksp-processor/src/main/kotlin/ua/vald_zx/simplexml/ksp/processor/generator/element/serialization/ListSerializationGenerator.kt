package ua.vald_zx.simplexml.ksp.processor.generator.element.serialization

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field
import ua.vald_zx.simplexml.ksp.processor.generator.FieldSerializer
import ua.vald_zx.simplexml.ksp.processor.generator.renderChildren

class ListSerializationGenerator(private val field: Field.List) : ElementSerializationGenerator {

    private var serializerName: String? = null
    private lateinit var genericArguments: String
    private lateinit var serializersMap: Map<Field, FieldSerializer>

    override fun render(
        funBuilder: FunSpec.Builder,
        fieldSerializer: FieldSerializer?,
        serializersMap: Map<Field, FieldSerializer>
    ) {
        serializerName = fieldSerializer?.serializerVariableName
        val genericTypesVariableName = fieldSerializer?.genericTypesVariableName
        this.genericArguments = if (genericTypesVariableName != null) ", $genericTypesVariableName" else ""
        this.serializersMap = serializersMap

        if (field.isNullable) {
            if (field.children.isNotEmpty()) {
                funBuilder.listNullableValueWithChildren()
            } else {
                funBuilder.listNullableValue()
            }
        } else {
            if (field.children.isNotEmpty()) {
                funBuilder.valueWithChildren()
            } else {
                funBuilder.value()
            }
        }
    }

    private fun FunSpec.Builder.printListForeach() {
        beginControlFlow("obj.${field.fieldName}.forEach")
        addStatement("$serializerName.buildXml(this, \"${field.entryName}\", it$genericArguments)")
        endControlFlow()
    }

    private fun FunSpec.Builder.listNullableValue() {
        if (field.isInline) {
            beginControlFlow("obj.${field.fieldName}?.forEach")
            addStatement("$serializerName.buildXml(this, \"${field.entryName}\", it$genericArguments)")
            endControlFlow()
        } else {
            beginControlFlow("obj.${field.fieldName}?.let")
            addStatement("list ->")
            beginControlFlow("tag(\"${field.tagName}\") {")
            beginControlFlow("list.forEach {")
            addStatement("$serializerName.buildXml(this, \"${field.entryName}\", it$genericArguments)")
            endControlFlow()
            endControlFlow()
            endControlFlow()
        }
    }

    private fun FunSpec.Builder.listNullableValueWithChildren() {
        if (field.isInline) {
            beginControlFlow("obj.${field.fieldName}?.forEach")
            addStatement("$serializerName.buildXml(this, \"${field.entryName}\", it$genericArguments)")
            endControlFlow()
        } else {
            beginControlFlow("obj.${field.fieldName}?.let")
            addStatement("list ->")
            beginControlFlow("tag(\"${field.tagName}\") {")
            renderChildren(field.children, serializersMap)
            beginControlFlow("list.forEach {")
            addStatement("$serializerName.buildXml(this, \"${field.entryName}\", it$genericArguments)")
            endControlFlow()
            endControlFlow()
            endControlFlow()
        }
    }

    private fun FunSpec.Builder.value() {
        if (field.isInline) {
            printListForeach()
        } else {
            beginControlFlow("tag(\"${field.tagName}\") {")
            printListForeach()
            endControlFlow()
        }
    }

    private fun FunSpec.Builder.valueWithChildren() {
        if (field.isInline) {
            printListForeach()
        } else {
            beginControlFlow("tag(\"${field.tagName}\") {")
            renderChildren(field.children, serializersMap)
            printListForeach()
            endControlFlow()
        }
    }
}