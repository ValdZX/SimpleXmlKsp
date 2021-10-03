package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.validate
import ua.vald_zx.simplexml.ksp.*
import ua.vald_zx.simplexml.ksp.processor.generator.generateModuleInitializer
import ua.vald_zx.simplexml.ksp.processor.generator.generateSerializer

class XmlSymbolProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val logger = environment.logger
    private val codeGenerator = environment.codeGenerator
    private val options = environment.options
    private val filesToGenerate = mutableMapOf<String, ClassToGenerate>()
    private var serializerSpecList = mutableSetOf<GeneratedSerializerSpec>()
    private val isStrictMode = options["strict"]?.toBoolean() ?: false
    private val scanBeanMode = options["scanBean"]?.toBoolean() ?: true
    private val visitor = ElementsVisitor(
        scanBeanMode = scanBeanMode,
        isStrictMode = isStrictMode,
        classToGenerateMap = filesToGenerate,
        logger = logger
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        listOfNotNull(
            Element::class.qualifiedName,
            Attribute::class.qualifiedName,
            ElementList::class.qualifiedName,
            ElementMap::class.qualifiedName,
            Text::class.qualifiedName,
            Root::class.qualifiedName,
        ).forEach { annotationName ->
            resolver.getSymbolsWithAnnotation(annotationName)
                .filter { it is KSPropertyDeclaration && it.validate() }
                .forEach { it.accept(visitor, Unit) }
        }

        if (filesToGenerate.isEmpty()) return emptyList()

        serializerSpecList.addAll(
            filesToGenerate.values
                .filter { toGenerate -> toGenerate.isValid(isStrictMode, logger) }
                .map { toGenerate -> codeGenerator.generateSerializer(toGenerate, logger) }
        )
        filesToGenerate.clear()
        return emptyList()
    }

    override fun finish() {
        val modulePackageArgument = options["simplexml.ksp.modulepackage"].orEmpty()
        val moduleNameArgument = options["simplexml.ksp.modulename"].orEmpty()
        var modulePackage = modulePackageArgument
        if (modulePackageArgument.isEmpty()) {
            serializerSpecList.forEach { spec ->
                modulePackage = findModulePackage(spec.packageName, modulePackage)
            }
        }
        if (serializerSpecList.isNotEmpty()) {
            codeGenerator.generateModuleInitializer(moduleNameArgument, modulePackage, serializerSpecList, logger)
        }
        super.finish()
    }

    private fun findModulePackage(fullName: String, modulePackage: String): String {
        return when {
            modulePackage.isEmpty() -> fullName
            fullName.contains(modulePackage) -> modulePackage
            else -> fullName.intersect(modulePackage)
        }
    }

    companion object {
        const val LIBRARY_PACKAGE = "ua.vald_zx.simplexml.ksp"
    }
}