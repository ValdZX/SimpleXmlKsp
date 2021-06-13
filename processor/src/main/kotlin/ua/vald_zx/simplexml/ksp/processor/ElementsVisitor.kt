package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid

class ElementsVisitor(
    private val filesToGenerate: MutableMap<String, BeanToGenerate>
) :
    KSVisitorVoid() {
    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        val parent = property.parentDeclaration
        if (parent !is KSClassDeclaration) return
        val parentName = parent.fullName
        var path = ""
        var name = ""
        var required = true
        property.annotations.forEach { annotation ->
            val shortName = annotation.shortName.getShortName()
            if (shortName == "Path") {
                path = annotation.arguments[0].value as String
            } else if (shortName == "Element") {
                name = annotation.arguments[0].value as String
                required = annotation.arguments[1].value as Boolean? ?: true
            }
        }
        filesToGenerate.getOrPut(parentName) {
            val parentShortName = parent.simpleName.getShortName()
            val rootName = parent.annotations
                .filter { annotation -> annotation.shortName.getShortName() == "Root" }
                .firstOrNull()?.arguments?.getOrNull(0)?.value?.toString() ?: parentShortName
            BeanToGenerate(
                fullName = parentName,
                name = parentShortName,
                rootName = rootName,
                packagePath = parent.packageName.asString()
            )
        }.fields.add(
            FieldToGenerate(
                path = path,
                fieldName = property.simpleName.asString(),
                tagName = name,
                type = FieldType.OBJECT,
                required = required
            )
        )
    }
}