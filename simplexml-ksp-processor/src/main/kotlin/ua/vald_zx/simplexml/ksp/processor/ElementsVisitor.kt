package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import ua.vald_zx.simplexml.ksp.*
import kotlin.reflect.KClass

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
        var elementName = ""
        var entryListName = ""
        var required = true
        var inline = false
        var converterType: KSType? = null
        val propertyType = property.type
        var propertyEntryType: KSTypeReference? = null
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
                        val shortName = arg.name?.getShortName()
                        when (shortName) {
                            "name" -> {
                                elementName = arg.value.asString(default = propertyName)
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
                                elementName = arg.value.asString(default = propertyName)
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
                    if (property.type.resolve().declaration.simpleName.asString() != "List") {
                        error("$parentName failure. Illegal annotation on $propertyName. ElementList only for kotlin.collections.List")
                    }
                    annotation.arguments.forEach { arg ->
                        when (arg.name?.getShortName()) {
                            "name" -> {
                                elementName = arg.value.asString(default = propertyName)
                            }
                            "entry" -> {
                                entryListName = arg.value.asString()
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
                    propertyEntryType = propertyType.element?.typeArguments?.first()?.type
                }
                Convert::class.simpleName -> {
                    converterType = annotation.arguments.first().value as KSType
                }
            }
        }
        val constructorParameters = parent.primaryConstructor?.parameters
        val requiredToConstructor = constructorParameters
            ?.find { it.name?.asString() == propertyName }
            ?.hasDefault?.not() ?: false
        if (requiredToConstructor && !required) {
            error("$parentName::$propertyName is not required without default value")
        }
        if (type == XmlUnitType.LIST && entryListName.isEmpty()) {
            entryListName =
                propertyType.element?.typeArguments?.first()?.type?.resolve()?.declaration?.simpleName?.getShortName()
                    ?: error(
                        "$parentName::$propertyName invalid list"
                    )
        }
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
        }.propertyElements.add(
            PropertyElement(
                propertyName = propertyName,
                xmlName = elementName,
                listEntryName = entryListName,
                xmlType = type,
                propertyType = propertyType,
                xmlPath = path,
                required = required,
                requiredToConstructor = requiredToConstructor,
                inlineList = inline,
                converterType = converterType,
                propertyEntryType = propertyEntryType
            )
        )
    }
}

private fun Any?.asString(default: String = ""): String {
    return if (this is String && this.isNotEmpty()) this else default
}

private fun Any?.asBoolean(default: Boolean = false): Boolean {
    return (this as Boolean?) ?: default
}