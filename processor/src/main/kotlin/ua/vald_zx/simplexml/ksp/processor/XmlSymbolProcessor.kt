package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.Element
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class XmlSymbolProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {

    private val logger = environment.logger
    private val codeGenerator = environment.codeGenerator
    private val filesToGenerate = mutableMapOf<String, ClassToGenerateExtension>()
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
        val fileBuilder = FileSpec.builder("ua.vald_zx.xml", "XmlExtensions")
        filesToGenerate.forEach { (fullName, classToGenerate) ->
            logger.info("processing $fullName")
            val beanName = classToGenerate.xmlBean.simpleName.asString()
            fileBuilder.addFunction(
                FunSpec.builder("toXml")
                    .receiver(
                        ClassName(
                            classToGenerate.xmlBean.packageName.asString(),
                            beanName
                        )
                    )
                    .returns(String::class)
                    .addStatement("return \"$fullName\"")
                    .build()
            )
            fileBuilder.addFunction(
                FunSpec.builder("parse$beanName")
                    .receiver(String::class)
                    .returns(
                        ClassName(
                            classToGenerate.xmlBean.packageName.asString(),
                            beanName
                        )
                    )
                    .addStatement("return ${beanName}()")
                    .build()
            )
        }
        val file = fileBuilder.build()
        codeGenerator.createNewFile(
            Dependencies(false),
            "ua.vald_zx.xml",
            "XmlExtensions"
        ).use { stream ->
            OutputStreamWriter(stream, StandardCharsets.UTF_8).use { writer -> file.writeTo(writer) }
        }
        super.finish()
    }
}