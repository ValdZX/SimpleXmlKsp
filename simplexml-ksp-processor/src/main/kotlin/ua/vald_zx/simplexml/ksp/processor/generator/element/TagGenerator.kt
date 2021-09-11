package ua.vald_zx.simplexml.ksp.processor.generator.element

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field
import ua.vald_zx.simplexml.ksp.processor.generator.FieldSerializer
import ua.vald_zx.simplexml.ksp.processor.generator.renderChildren

internal class TagGenerator(private val field: Field.Tag) : ElementGenerator {

    private var serializerName: String? = null
    private lateinit var genericArguments: String
    private lateinit var serializersMap: Map<Field, FieldSerializer>
    private val tabName = field.tagName
    private val fieldName = field.fieldName
    private val children = field.children

    override fun render(
        funBuilder: FunSpec.Builder,
        fieldSerializer: FieldSerializer?,
        serializersMap: Map<Field, FieldSerializer>
    ) {
        val serializerName = fieldSerializer?.serializerVariableName
        val genericTypesVariableName = fieldSerializer?.genericTypesVariableName
        this.genericArguments = if (genericTypesVariableName != null) ", $genericTypesVariableName" else ""
        this.serializerName = serializerName
        this.serializersMap = serializersMap

        if (children.isEmpty()) {
            if (field.isNullable) {
                funBuilder.nullableValue()
            } else {
                funBuilder.value()
            }
        } else {
            if (field.isNullable) {
                funBuilder.nullableValueWithChildren()
            } else {
                funBuilder.valueWithChildren()
            }
        }
    }

    private fun FunSpec.Builder.value() {
        addStatement("${serializerName}.buildXml(this, \"$tabName\", obj.$fieldName)$genericArguments")
    }

    private fun FunSpec.Builder.nullableValue() {
        if (field.required) {
            addStatement("val $fieldName = obj.$fieldName?: throw SerializeException(\"\"\"field $tabName value is required\"\"\")")
            addStatement("$serializerName.buildXml(this, \"$tabName\", $fieldName$genericArguments)")
        } else {
            beginControlFlow("obj.$fieldName?.let")
            addStatement("$serializerName.buildXml(this, \"$tabName\", it$genericArguments)")
            endControlFlow()
        }
    }

    private fun FunSpec.Builder.valueWithChildren() {
        if (fieldName.isEmpty()) {
            beginControlFlow("tag(\"$tabName\")")
        } else {
            beginControlFlow("$serializerName.buildXml(this, \"$tabName\", obj.$fieldName$genericArguments)")
        }
        renderChildren(children, serializersMap)
        endControlFlow()
    }

    private fun FunSpec.Builder.nullableValueWithChildren() {
        addStatement("val $fieldName = obj.$fieldName")
        beginControlFlow("if ($fieldName != null)")
        beginControlFlow("$serializerName.buildXml(this, \"$tabName\", $fieldName$genericArguments)")
        renderChildren(children, serializersMap)
        endControlFlow()
        nextControlFlow("else")
        beginControlFlow("tag(\"$tabName\")")
        renderChildren(children, serializersMap)
        endControlFlow()
        endControlFlow()
    }
}