package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import ua.vald_zx.simplexml.ksp.Attribute
import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.ElementList
import ua.vald_zx.simplexml.ksp.Path

class ElementsVisitor(
    private val classToGenerate: MutableMap<String, ClassToGenerate>,
    private val logger: KSPLogger
) :
    KSVisitorVoid() {
    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        val parent = property.parentDeclaration
        if (parent !is KSClassDeclaration) return
        val parentName = parent.fullName
        val propertyName = property.simpleName.asString()
        logger.info("Visited $parentName property $propertyName")
        var path = ""
        var name = ""
        var required = true
        var inline = false
        var type: XmlUnitType = XmlUnitType.UNKNOWN
        property.annotations.forEach { annotation ->
            when (annotation.shortName.asString()) {
                Path::class.simpleName        -> {
                    path = annotation.arguments[0].value as String
                    if (path.isEmpty()) error("$parentName failure. Remove @Path or fill name on $propertyName")
                }
                Element::class.simpleName     -> {
                    name = annotation.arguments[0].value as String
                    required = annotation.arguments[1].value as Boolean? ?: false
                    if (type != XmlUnitType.UNKNOWN) error("$parentName failure. Illegal annotation on $propertyName")
                    type = XmlUnitType.TAG
                }
                Attribute::class.simpleName   -> {
                    name = annotation.arguments[0].value as String
                    required = annotation.arguments[1].value as Boolean? ?: false
                    if (type != XmlUnitType.UNKNOWN) error("$parentName failure. Illegal annotation on $propertyName")
                    type = XmlUnitType.ATTRIBUTE
                }
                ElementList::class.simpleName -> {
                    name = annotation.arguments[0].value as String
                    inline = annotation.arguments[1].value as Boolean? ?: false
                    required = annotation.arguments[2].value as Boolean? ?: false
                    if (type != XmlUnitType.UNKNOWN) error("$parentName failure. Illegal annotation on $propertyName")
                    type = XmlUnitType.LIST
                }
            }
        }
        classToGenerate.getOrPut(parentName) {
            val parentShortName = parent.simpleName.getShortName()
            val rootName = parent.annotations
                .filter { annotation -> annotation.shortName.getShortName() == "Root" }
                .firstOrNull()?.arguments?.getOrNull(0)?.value?.toString() ?: parentShortName
            ClassToGenerate(
                fullName = parentName,
                name = parentShortName,
                rootName = rootName,
                packagePath = parent.packageName.asString()
            )
        }.properties.add(
            Property(
                path = path,
                propertyName = propertyName,
                unitName = name,
                xmlType = type,
                type = property.type,
                required = required,
                inline = inline
            )
        )
    }
}