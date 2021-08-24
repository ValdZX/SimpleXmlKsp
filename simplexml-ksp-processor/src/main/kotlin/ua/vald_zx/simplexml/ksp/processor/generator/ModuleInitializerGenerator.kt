package ua.vald_zx.simplexml.ksp.processor.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import ua.vald_zx.simplexml.ksp.GlobalSerializersLibrary
import ua.vald_zx.simplexml.ksp.processor.GeneratedSerializerSpec
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

fun CodeGenerator.generateModuleInitializer(
    moduleName: String,
    modulePackage: String,
    toRegister: List<GeneratedSerializerSpec>,
    logger: KSPLogger
) {
    val fileName = "${moduleName}SerializersEnrolment"
    val file = FileSpec.builder(modulePackage, fileName)
        .addImport(GlobalSerializersLibrary::class, "")
        .addRegistrationImports(toRegister)
        .addType(
            TypeSpec.objectBuilder(fileName)
                .addFunction(
                    FunSpec.builder("enrol")
                        .apply {
                            toRegister.forEach { toRegistration ->
                                beginControlFlow("GlobalSerializersLibrary.add(${toRegistration.beanClass.simpleName}::class)")
                                addStatement(toRegistration.serializerClass.simpleName)
                                endControlFlow()
                            }
                        }.build()
                ).build()
        ).build()
    createNewFile(
        Dependencies(false),
        modulePackage,
        fileName
    ).use { stream ->
        OutputStreamWriter(stream, StandardCharsets.UTF_8).use { writer ->
            file.writeTo(writer)
        }
    }
    logger.info("Generated $modulePackage.$fileName")
}

private fun FileSpec.Builder.addRegistrationImports(toRegister: List<GeneratedSerializerSpec>): FileSpec.Builder {
    toRegister.forEach { toRegistration ->
        addImport(toRegistration.beanClass, "")
        addImport(toRegistration.serializerClass, "")
    }
    return this
}