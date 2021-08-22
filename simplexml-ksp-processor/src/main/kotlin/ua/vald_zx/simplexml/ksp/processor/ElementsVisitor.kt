package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Nullability
import ua.vald_zx.simplexml.ksp.Root

class ElementsVisitor(
    private val isStrictMode: Boolean,
    private val classToGenerate: MutableMap<String, ClassToGenerate>,
    private val logger: KSPLogger
) : KSVisitorVoid() {
    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        val parent = property.parentDeclaration
        if (parent !is KSClassDeclaration) return
        val parentName = parent.fullName
        val propertyName = property.simpleName.asString()
        val propertyType = property.type
        val annotationInfo = property.getAnnotationInfo(logger, isStrictMode, propertyType, parentName, propertyName)
        val constructorParameters = parent.primaryConstructor?.parameters
        val constructorParameter = constructorParameters?.find { it.name?.asString() == propertyName }
        val isVariable = property.isMutable
        val isConstructorParameter = constructorParameter != null
        val hasDefaultValue = constructorParameter?.hasDefault ?: false
        val requiredToConstructor = constructorParameter?.hasDefault?.not() ?: false
        if (requiredToConstructor && !annotationInfo.required && propertyType.resolve().nullability == Nullability.NOT_NULL) {
            error("$parentName::$propertyName is not required without default value")
        }
        classToGenerate.getOrPut(parentName) {
            val parentShortName = parent.simpleName.getShortName()
            val rootName = parent.annotations
                .filter { annotation -> annotation.shortName.getShortName() == Root::class.simpleName }
                .firstOrNull()?.arguments?.getOrNull(0)?.value?.toString() ?: parentShortName
            ClassToGenerate(
                bean = parent,
                typeParameters = parent.typeParameters,
                fullName = parentName,
                name = parentShortName,
                rootName = rootName,
                packagePath = parent.packageName.asString()
            )
        }.propertyElements.add(
            PropertyElement(
                propertyName = propertyName,
                xmlName = annotationInfo.elementName,
                listEntryName = annotationInfo.entryListName,
                xmlType = annotationInfo.type,
                propertyType = propertyType,
                xmlPath = annotationInfo.path,
                required = annotationInfo.required,
                isVariable = isVariable,
                isConstructorParameter = isConstructorParameter,
                hasDefaultValue = hasDefaultValue,
                inlineList = annotationInfo.inline,
                converterType = annotationInfo.converterType,
                propertyEntryType = annotationInfo.propertyEntryType
            )
        )
    }
}