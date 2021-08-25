package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import ua.vald_zx.simplexml.ksp.Root

class ElementsVisitor(
    private val scanBeanMode: Boolean,
    private val isStrictMode: Boolean,
    private val classToGenerateMap: MutableMap<String, ClassToGenerate>,
    private val logger: KSPLogger
) : KSVisitorVoid() {
    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        val parent = property.parentDeclaration
        if (parent !is KSClassDeclaration) return
        if (scanBeanMode) {
            readClassDeclaration(parent)
        } else {
            val parentName = parent.fullName
            val propertyElement = property.toElement(parent) ?: return
            classToGenerateMap
                .getOrPut(parentName) { parent.toGenerate() }
                .propertyElements.add(propertyElement)
        }
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        readClassDeclaration(classDeclaration)
    }

    private fun readClassDeclaration(classDeclaration: KSClassDeclaration) {
        val parentName = classDeclaration.fullName
        if (classToGenerateMap.contains(parentName)) return
        if (classDeclaration.isAbstract()) return
        val propertyElements = classDeclaration.getAllProperties().mapNotNull { property ->
            property.toElement(classDeclaration)
        }.toMutableList()
        val classToGenerate = classDeclaration.toGenerate(propertyElements)
        classToGenerateMap[parentName] = classToGenerate
    }

    private fun KSClassDeclaration.toGenerate(propertyElements: MutableList<PropertyElement> = mutableListOf()): ClassToGenerate {
        val parentShortName = simpleName.getShortName()
        val rootName = annotations
            .filter { annotation -> annotation.shortName.getShortName() == Root::class.simpleName }
            .firstOrNull()?.arguments?.getOrNull(0)?.value?.toString() ?: parentShortName
        return ClassToGenerate(
            bean = this,
            typeParameters = typeParameters,
            fullName = fullName,
            name = parentShortName,
            rootName = rootName,
            packagePath = packageName.asString(),
            propertyElements = propertyElements
        )
    }

    private fun KSPropertyDeclaration.toElement(parent: KSClassDeclaration): PropertyElement? {
        val parentName = parent.fullName
        val propertyName = simpleName.asString()
        val propertyType = type
        val annotationInfo = getAnnotationInfo(logger, isStrictMode, propertyType, parentName, propertyName)
        if (annotationInfo.type == XmlUnitType.UNKNOWN) return null
        val constructorParameters = parent.primaryConstructor?.parameters
        val constructorParameter = constructorParameters?.find { it.name?.asString() == propertyName }
        val isVariable = isMutable
        val isMutableCollection = type.isMutableCollection()
        val isConstructorParameter = constructorParameter != null
        val hasDefaultValue = constructorParameter?.hasDefault ?: false
        val requiredToConstructor = constructorParameter?.hasDefault?.not() ?: false
        if (requiredToConstructor && !annotationInfo.required && propertyType.resolve().nullability == Nullability.NOT_NULL) {
            error("$parentName::$propertyName is not required without default value")
        }
        return PropertyElement(
            propertyName = propertyName,
            xmlName = annotationInfo.elementName,
            listEntryName = annotationInfo.entryListName,
            keyMapName = annotationInfo.keyMapName,
            valueMapName = annotationInfo.valueMapName,
            xmlType = annotationInfo.type,
            propertyType = propertyType,
            xmlPath = annotationInfo.path,
            required = annotationInfo.required,
            isVariable = isVariable,
            isMutableCollection = isMutableCollection,
            isConstructorParameter = isConstructorParameter,
            hasDefaultValue = hasDefaultValue,
            inlineList = annotationInfo.inline,
            converterType = annotationInfo.converterType,
            propertyEntryType = annotationInfo.propertyEntryType,
            propertyKeyType = annotationInfo.propertyKeyType,
            propertyValueType = annotationInfo.propertyValueType
        )
    }

    private fun KSTypeReference.isMutableCollection(): Boolean {
        val typeSimpleName = resolve().declaration.simpleName.asString()
        return typeSimpleName == "MutableList" || typeSimpleName == "MutableMap"
    }
}