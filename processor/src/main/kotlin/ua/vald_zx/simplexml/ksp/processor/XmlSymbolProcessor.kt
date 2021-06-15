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
import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.GlobalSerializersLibrary
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class XmlSymbolProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {

    private val logger = environment.logger
    private val codeGenerator = environment.codeGenerator
    private val options = environment.options
    private val filesToGenerate = mutableMapOf<String, BeanToGenerate>()
    private val visitor = ElementsVisitor(filesToGenerate)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val elementName = Element::class.qualifiedName ?: error("qualifiedName not recognized")
        resolver.getSymbolsWithAnnotation(elementName)
            .filter { it is KSPropertyDeclaration && it.validate() }
            .forEach { it.accept(visitor, Unit) }
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
                .addImport("ua.vald_zx.simplexml.ksp.xml", "tag")
                .addImport("ua.vald_zx.simplexml.ksp.xml.XmlReader", "readXml")
                .addImport("ua.vald_zx.simplexml.ksp.xml.error", "InvalidXml")
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
                    file.writeTo(
                        writer
                    )
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

    private fun FunSpec.Builder.generateSerialization(beanToGenerate: BeanToGenerate): FunSpec.Builder {
        val statementBuilder = StringBuilder()
        statementBuilder.appendLine("return tag(\"${beanToGenerate.rootName}\") {")
        beanToGenerate.toDom().renderChildren(statementBuilder, 1)
        statementBuilder.appendLine("}.render()")
        addStatement(statementBuilder.toString())
        return this
    }

    private fun FunSpec.Builder.generateDeserialization(beanToGenerate: BeanToGenerate): FunSpec.Builder {
        val statementBuilder = StringBuilder()
        val margin = " ".repeat(4)
        val dom = beanToGenerate.toDom()
        statementBuilder.appendLine("val dom = raw.readXml() ?: throw InvalidXml()")
        val fieldToValueMap: MutableMap<String, String> = mutableMapOf()
        dom.generateValues(statementBuilder, fieldToValueMap, "dom", 0)
        statementBuilder.appendLine("return ${beanToGenerate.name}(")
        beanToGenerate.fields.forEach { field ->
            statementBuilder.appendLine("${margin}${field.fieldName} = ${fieldToValueMap[field.fieldName]}.text,")
        }
        statementBuilder.appendLine(")")
        addStatement(statementBuilder.toString())
        return this
    }

    private fun Iterable<FieldElement>.renderChildren(builder: StringBuilder, offset: Int) {
        val margin = " ".repeat(offset * 4)
        forEach { field ->
            if (field.children.isNotEmpty()) {
                builder.appendLine("${margin}tag(\"${field.tagName}\") {")
                field.children.renderChildren(builder, offset + 1)
                builder.appendLine("${margin}}")
            } else {
                builder.appendLine("${margin}tag(\"${field.tagName}\", obj.${field.fieldName})")
            }
        }
    }

    private fun BeanToGenerate.toDom(): List<FieldElement> {
        val firstLayerFields = fields
            .filter { it.path.isEmpty() }
            .map { FieldElement(it.tagName, it.fieldName, isValueTag = true) }.toMutableList()
        fields.filter {
            it.path.isNotEmpty()
        }.map { field ->
            val path = field.path.split("/")
            addLayer(firstLayerFields, path[0], path.subList(1, path.size), field)
        }
        return firstLayerFields
    }

    private fun addLayer(
        currentLayerFields: MutableList<FieldElement>,
        currentLayerTag: String,
        path: List<String>,
        field: FieldToGenerate
    ) {
        if (currentLayerTag.isEmpty()) {
            if (currentLayerFields.any { it.tagName == field.tagName }) {
                error("already has tag with name ${field.tagName}")
            } else {
                currentLayerFields.add(
                    FieldElement(
                        field.tagName,
                        field.fieldName,
                        isValueTag = true
                    )
                )
            }
        } else {
            val currentPathTag =
                currentLayerFields.find {
                    it.tagName == currentLayerTag
                } ?: FieldElement(
                    currentLayerTag,
                    isValueTag = false
                ).apply { currentLayerFields.add(this) }
            if (currentPathTag.isValueTag) error("already has field with name ${currentPathTag.tagName}")
            if (path.isEmpty()) {
                addLayer(currentPathTag.children, "", emptyList(), field)
            } else {
                addLayer(currentPathTag.children, path[0], path.subList(1, path.size), field)
            }
        }
    }

    private fun List<FieldElement>.generateValues(
        statementBuilder: StringBuilder,
        fieldToValueMap: MutableMap<String, String>,
        parentValueName: String,
        layer: Int
    ) {
        forEachIndexed { index, element ->
            val currentValueName = "layer${layer}Tag$index"
            statementBuilder.appendLine("val $currentValueName = $parentValueName[\"${element.tagName}\"]")
            if (element.isValueTag) {
                fieldToValueMap[element.fieldName] = currentValueName
            } else {
                element.children.generateValues(statementBuilder, fieldToValueMap, currentValueName, layer + 1)
            }
        }
    }
}