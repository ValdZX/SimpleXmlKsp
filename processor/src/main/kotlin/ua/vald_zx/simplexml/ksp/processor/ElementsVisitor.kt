package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import ua.vald_zx.simplexml.ksp.*

class ElementsVisitor(
    private val classToGenerate: MutableMap<String, ClassToGenerate>,
    private val logger: KSPLogger
) : KSVisitorVoid() {
    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        val parent = property.parentDeclaration
        if (parent !is KSClassDeclaration) return
        val parentName = parent.fullName
        val propertyName = property.simpleName.asString()
        logger.info("Visited $parentName property $propertyName")
        var path = ""
        var name = ""
        var entry = ""
        var required = true
        var inline = false
        var type: XmlUnitType = XmlUnitType.UNKNOWN
        property.annotations.forEach { annotation ->
            when (annotation.shortName.asString()) {
                Path::class.simpleName -> {
                    annotation.arguments.forEach { arg ->
                        when (arg.name?.getShortName()) {
                            "path" -> {
                                path = arg.value.asString()
                                if (path.isEmpty()) error("$parentName failure. Remove @Path or fill name on $propertyName")
                            }
                        }
                    }
                }
                Element::class.simpleName -> {
                    annotation.arguments.forEach { arg ->
                        when (arg.name?.getShortName()) {
                            "name" -> {
                                name = arg.value.asString(default = propertyName)
                            }
                            "required" -> {
                                required = arg.value.asBoolean(default = true)
                            }
                        }
                    }
                    if (type != XmlUnitType.UNKNOWN) error("$parentName failure. Illegal annotation on $propertyName")
                    type = XmlUnitType.TAG
                }
                Attribute::class.simpleName -> {
                    annotation.arguments.forEach { arg ->
                        when (arg.name?.getShortName()) {
                            "name" -> {
                                name = arg.value.asString(default = propertyName)
                            }
                            "required" -> {
                                required = arg.value.asBoolean(default = true)
                            }
                        }
                    }
                    if (type != XmlUnitType.UNKNOWN) error("$parentName failure. Illegal annotation on $propertyName")
                    type = XmlUnitType.ATTRIBUTE
                }
                ElementList::class.simpleName -> {
                    annotation.arguments.forEach { arg ->
                        when (arg.name?.getShortName()) {
                            "name" -> {
                                name = arg.value.asString(default = propertyName)
                            }
                            "entry" -> {
                                entry = arg.value.asString()
                            }
                            "required" -> {
                                required = arg.value.asBoolean(default = true)
                            }
                            "inline" -> {
                                inline = arg.value.asBoolean()
                            }
                        }
                    }
                    if (type != XmlUnitType.UNKNOWN) error("$parentName failure. Illegal annotation on $propertyName")
                    type = XmlUnitType.LIST
                }
            }
        }
        val hasDefault = parent.primaryConstructor
            ?.parameters
            ?.any { it.name?.asString() == propertyName && it.hasDefault }
            ?: true
        if (!hasDefault && !required) error("$parentName::$propertyName is not required without default value")
        classToGenerate.getOrPut(parentName) {
            val parentShortName = parent.simpleName.getShortName()
            val rootName = parent.annotations
                .filter { annotation -> annotation.shortName.getShortName() == Root::class.simpleName }
                .firstOrNull()?.arguments?.getOrNull(0)?.value?.toString() ?: parentShortName
            ClassToGenerate(
                bean = parent,
                fullName = parentName,
                name = parentShortName,
                rootName = rootName,
                packagePath = parent.packageName.asString()
            )
        }.properties.add(
            Property(
                path = path,
                name = propertyName,
                xmlName = name,
                entry = entry,
                xmlType = type,
                type = property.type,
                required = required,
                inline = inline,
                hasDefault = hasDefault
            )
        )
    }
}

private fun Any?.asString(default: String = ""): String {
    return (this as String?) ?: default
}

private fun Any?.asBoolean(default: Boolean = false): Boolean {
    return (this as Boolean?) ?: default
}