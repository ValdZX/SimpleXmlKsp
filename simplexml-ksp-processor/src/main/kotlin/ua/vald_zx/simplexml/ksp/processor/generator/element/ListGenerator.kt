package ua.vald_zx.simplexml.ksp.processor.generator.element

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field
import ua.vald_zx.simplexml.ksp.processor.generator.FieldSerializer
import ua.vald_zx.simplexml.ksp.processor.generator.renderChildren

class ListGenerator(private val field: Field.List) : ElementGenerator {

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
            funBuilder.listNullableValue()
        } else {
            if (field.children.isNotEmpty()) {
                if (field.isInline) {
                    funBuilder.printListForeach()
                } else {
                    funBuilder.beginControlFlow("tag(\"${field.tagName}\") {")
                    funBuilder.renderChildren(field.children, serializersMap)
                    funBuilder.printListForeach()
                    funBuilder.endControlFlow()
                }
            } else {
                if (field.isInline) {
                    funBuilder.printListForeach()
                } else {
                    funBuilder.beginControlFlow("tag(\"${field.tagName}\") {")
                    funBuilder.printListForeach()
                    funBuilder.endControlFlow()
                }
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
}