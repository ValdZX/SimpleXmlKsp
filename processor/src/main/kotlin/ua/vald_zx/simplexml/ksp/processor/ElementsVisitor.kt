package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid

class ElementsVisitor(private val filesToGenerate: MutableMap<String, ClassToGenerateExtension>) : KSVisitorVoid() {
    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        val parent = property.parentDeclaration
        val parentName = if (parent is KSClassDeclaration) {
            parent.fullName
        } else return
        property.annotations.forEach { annotation ->
            val shortName = annotation.shortName.getShortName()
            var path = ""
            var name = ""
            var required = true
            if (shortName == "Path") {
                path = annotation.arguments[0].value as String
            } else if (shortName == "Element") {
                name = annotation.arguments[0].value as String
                required = annotation.arguments[1].value as Boolean? ?: true
            }
            filesToGenerate.getOrPut(parentName) {
                ClassToGenerateExtension(parent)
            }.elements.add(XmlElement(path = path, name = name, required = required))
        }
    }
}