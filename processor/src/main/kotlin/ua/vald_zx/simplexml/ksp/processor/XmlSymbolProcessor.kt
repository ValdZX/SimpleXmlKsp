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
import ua.vald_zx.simplexml.ksp.processor.generator.generateDeserialization
import ua.vald_zx.simplexml.ksp.processor.generator.generateSerialization
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
                                .generateDeserialization(classToGenerate, logger)
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

    companion object {
        const val LIBRARY_PACKAGE = "ua.vald_zx.simplexml.ksp.xml"
    }
}