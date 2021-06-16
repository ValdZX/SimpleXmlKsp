package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import ua.vald_zx.simplexml.ksp.Attribute
import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.ElementList
import ua.vald_zx.simplexml.ksp.GlobalSerializersLibrary
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class XmlSymbolProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {

    private val logger = environment.logger
    private val codeGenerator = environment.codeGenerator
    private val options = environment.options
    private val filesToGenerate = mutableMapOf<String, ClassToGenerate>()
    private val visitor = ElementsVisitor(filesToGenerate, logger)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        listOfNotNull(
            Element::class.qualifiedName,
            Attribute::class.qualifiedName,
            ElementList::class.qualifiedName,
        ).forEach { annotationName ->
            resolver.getSymbolsWithAnnotation(annotationName)
                .filter { it is KSPropertyDeclaration && it.validate() }
                .forEach { it.accept(visitor, Unit) }
        }
        return emptyList()
    }

    override fun finish() {
        if (filesToGenerate.isEmpty()) return

        val modulePackageArgument = options["ModulePackage"]
        val moduleName = options["ModuleName"].orEmpty()
        var modulePackage: String? = modulePackageArgument
        val toRegister = filesToGenerate.map { (_, classToGenerate) ->
            val beanName = classToGenerate.name
            val packageName = classToGenerate.packagePath
            if (modulePackageArgument.isNullOrEmpty()) {
                modulePackage = findModulePackage(packageName, modulePackage)
            }
            val beanClassName = ClassName(packageName, beanName)
            val objectName = beanName + "Serializer"
            logger.info("Generating $packageName.$objectName")
            val serializerClassName = ClassName(packageName, objectName)
            val file = FileSpec.builder(packageName, objectName)
                .addImport(LIBRARY_PACKAGE, "tag")
                .addImport("$LIBRARY_PACKAGE.XmlReader", "readXml")
                .addImport("$LIBRARY_PACKAGE.error", "InvalidXml")
                .addType(
                    TypeSpec.objectBuilder(objectName)
                        .addSuperinterface(
                            ClassName(
                                "ua.vald_zx.simplexml.ksp",
                                "Serializer"
                            ).parameterizedBy(beanClassName)
                        )
                        .addFunction(
                            FunSpec.builder("serialize")
                                .addModifiers(KModifier.OVERRIDE)
                                .returns(String::class)
                                .addParameter("obj", beanClassName)
                                .generateSerialization(classToGenerate)
                                .build()
                        ).addFunction(
                            FunSpec.builder("deserialize")
                                .addModifiers(KModifier.OVERRIDE)
                                .addParameter("raw", String::class)
                                .returns(beanClassName)
                                .generateDeserialization(classToGenerate)
                                .build()
                        ).build()
                ).build()
            codeGenerator.createNewFile(
                Dependencies(false),
                packageName,
                objectName
            ).use { stream ->
                OutputStreamWriter(stream, StandardCharsets.UTF_8).use { writer ->
                    file.writeTo(writer)
                }
            }
            ToRegistration(beanClassName, serializerClassName)
        }

        val modulePackageVal = modulePackage
        if (modulePackageVal != null) {
            val fileName = "${moduleName}ModuleInitializer"
            val file = FileSpec.builder(modulePackageVal, fileName)
                .addImport(GlobalSerializersLibrary::class, "")
                .addImports(toRegister)
                .addType(
                    TypeSpec.objectBuilder(fileName)
                        .addFunction(
                            FunSpec.builder("init")
                                .addStatement(
                                    toRegister.joinToString("\n") { toRegistration ->
                                        "GlobalSerializersLibrary.add(${toRegistration.beanClass.simpleName}::class) { ${toRegistration.serializerClass.simpleName} }"
                                    }
                                ).build()
                        ).build()
                ).build()
            codeGenerator.createNewFile(
                Dependencies(false),
                modulePackageVal,
                fileName
            ).use { stream ->
                OutputStreamWriter(stream, StandardCharsets.UTF_8).use { writer ->
                    file.writeTo(
                        writer
                    )
                }
            }
        }
        super.finish()
    }

    private fun findModulePackage(fullName: String, modulePackage: String?): String {
        return when {
            modulePackage == null -> fullName
            fullName.contains(modulePackage) -> modulePackage
            else -> fullName.intersect(modulePackage)

        }
    }

    private fun FileSpec.Builder.addImports(toRegister: List<ToRegistration>): FileSpec.Builder {
        toRegister.forEach { toRegistration ->
            addImport(toRegistration.beanClass, "")
            addImport(toRegistration.serializerClass, "")
        }
        return this
    }

    private fun FunSpec.Builder.generateSerialization(classToGenerate: ClassToGenerate): FunSpec.Builder {
        val statementBuilder = StringBuilder()
        statementBuilder.appendLine("return tag(\"${classToGenerate.rootName}\") {")
        classToGenerate.toDom().renderChildren(statementBuilder, 1)
        statementBuilder.appendLine("}.render()")
        addStatement(statementBuilder.toString())
        return this
    }

    private fun FunSpec.Builder.generateDeserialization(classToGenerate: ClassToGenerate): FunSpec.Builder {
        val statementBuilder = StringBuilder()
        val margin = " ".repeat(4)
        val dom = classToGenerate.toDom()
        statementBuilder.appendLine("val dom = raw.readXml() ?: throw InvalidXml()")
        val fieldToValueMap: MutableMap<String, String> = mutableMapOf()
        dom.generateValues(statementBuilder, fieldToValueMap, "dom", 0)
        statementBuilder.appendLine("return ${classToGenerate.name}(")
        classToGenerate.properties.forEach { field ->
            statementBuilder.appendLine("${margin}${field.propertyName} = ${fieldToValueMap[field.propertyName]}.text,")
        }
        statementBuilder.appendLine(")")
        addStatement(statementBuilder.toString())
        return this
    }

    private fun Iterable<FieldElement>.renderChildren(builder: StringBuilder, offset: Int) {
        val margin = " ".repeat(offset * 4)
        forEach { field ->
            if (field.children.isNotEmpty()) {
                if (field.propertyName.isEmpty()) {
                    builder.appendLine("${margin}tag(\"${field.name}\") {")
                } else {
                    builder.appendLine("${margin}tag(\"${field.name}\", obj.${field.propertyName}) {")
                }
                field.children.renderChildren(builder, offset + 1)
                builder.appendLine("${margin}}")
            } else {
                if (field.type == XmlUnitType.TAG) {
                    builder.appendLine("${margin}tag(\"${field.name}\", obj.${field.propertyName})")
                } else if (field.type == XmlUnitType.ATTRIBUTE) {
                    builder.appendLine("${margin}attr(\"${field.name}\", obj.${field.propertyName})")
                }
            }
        }
    }

    private fun ClassToGenerate.toDom(): List<FieldElement> {
        val firstLayerFields = properties
            .filter { it.path.isEmpty() }
            .map { FieldElement(it.unitName, it.propertyName) }.toMutableList()
        properties.filter {
            it.path.isNotEmpty()
        }.map { field ->
            val path = field.path.split("/")
            addLayer(firstLayerFields, path[0], path.subList(1, path.size), field)
        }
        return firstLayerFields
    }

    private fun addLayer(
        currentLayerFields: MutableList<FieldElement>,
        currentPath: String,
        path: List<String>,
        field: Property
    ) {
        if (currentPath.isEmpty()) {
            val layerField = currentLayerFields.find { it.name == field.unitName }
            if (layerField != null && (layerField.propertyName.isNotEmpty() || layerField.children.any { it.type != XmlUnitType.ATTRIBUTE })) {
                error("already has data tag with name ${field.unitName}")
            } else if (layerField?.propertyName?.isEmpty() == true) {
                val itemIndex = currentLayerFields.indexOf(layerField)
                currentLayerFields[itemIndex] = layerField.copy(propertyName = field.propertyName)
            } else {
                currentLayerFields.add(
                    FieldElement(
                        field.unitName,
                        field.propertyName,
                        type = field.xmlType
                    )
                )
            }
        } else {
            val currentPathUnit = currentLayerFields.find { it.name == currentPath }
                ?: FieldElement(currentPath)
                    .apply { currentLayerFields.add(this) }
            if (field.xmlType == XmlUnitType.ATTRIBUTE && path.isEmpty()) {
                currentPathUnit.children.add(
                    FieldElement(
                        field.unitName,
                        field.propertyName,
                        type = field.xmlType
                    )
                )
            } else if (path.isEmpty()) {
                addLayer(currentPathUnit.children, "", emptyList(), field)
            } else {
                addLayer(currentPathUnit.children, path[0], path.subList(1, path.size), field)
            }
        }
    }

    private fun List<FieldElement>.generateValues(
        statementBuilder: StringBuilder,
        fieldToValueMap: MutableMap<String, String>,
        parentValueName: String,
        layer: Int,
        iterator: Iterator<Int> = sequence {
            var counter = 0
            while (true) {
                yield(counter)
                counter++
            }
        }.iterator()
    ) {
        forEach { element ->
            if (element.type == XmlUnitType.TAG) {
                val currentValueName = "layer${layer}Tag${iterator.next()}"
                statementBuilder.appendLine("val $currentValueName = $parentValueName[\"${element.name}\"]")
                fieldToValueMap[element.propertyName] = currentValueName
                element.children.generateValues(
                    statementBuilder,
                    fieldToValueMap,
                    currentValueName,
                    layer + 1,
                    iterator
                )
            } else if (element.type == XmlUnitType.ATTRIBUTE) {
                val currentValueName = "layer${layer}Attribute${iterator.next()}"
                statementBuilder.appendLine("val $currentValueName = $parentValueName.attribute(\"${element.name}\")")
                fieldToValueMap[element.propertyName] = currentValueName
            }
        }
    }

    companion object {
        const val LIBRARY_PACKAGE = "ua.vald_zx.simplexml.ksp.xml"
    }
}