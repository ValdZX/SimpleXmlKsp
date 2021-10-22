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
            val propertyElement = property.toElement(parent)
            classToGenerateMap
                .getOrPut(parentName) { parent.toGenerate() }
                .fields.add(propertyElement)
        }
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        readClassDeclaration(classDeclaration)
    }

    private fun readClassDeclaration(classDeclaration: KSClassDeclaration) {
        val parentName = classDeclaration.fullName
        if (classToGenerateMap.contains(parentName)) return
        if (classDeclaration.isAbstract()) return
        val fields = classDeclaration.getAllProperties().map { property ->
            property.toElement(classDeclaration)
        }.toMutableList()
        val classToGenerate = classDeclaration.toGenerate(fields)
        classToGenerateMap[parentName] = classToGenerate
    }

    private fun KSClassDeclaration.toGenerate(fields: MutableList<Field> = mutableListOf()): ClassToGenerate {
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
            fields = fields
        )
    }

    private fun KSPropertyDeclaration.toElement(parent: KSClassDeclaration): Field {
        return AnnotationInfoReader(
            this,
            parent,
            logger,
            isStrictMode,
        ).read()
    }
}