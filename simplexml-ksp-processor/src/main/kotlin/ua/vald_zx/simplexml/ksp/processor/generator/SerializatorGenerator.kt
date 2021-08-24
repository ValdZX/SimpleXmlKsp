package ua.vald_zx.simplexml.ksp.processor.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import ua.vald_zx.simplexml.ksp.processor.ClassToGenerate
import ua.vald_zx.simplexml.ksp.processor.GeneratedSerializerSpec
import ua.vald_zx.simplexml.ksp.processor.XmlSymbolProcessor
import ua.vald_zx.simplexml.ksp.xml.TagFather
import ua.vald_zx.simplexml.ksp.xml.model.XmlElement
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

fun CodeGenerator.generateSerializer(
    classToGenerate: ClassToGenerate,
    logger: KSPLogger
): GeneratedSerializerSpec {
    val typeParameters = classToGenerate.typeParameters
    val beanName = classToGenerate.name
    val packageName = classToGenerate.packagePath
    val beanClassName = ClassName(packageName, beanName)
    var parameterizedStarsBeanClassName: ParameterizedTypeName? = null
    var parameterizedAnyBeanClassName: ParameterizedTypeName? = null
    if (typeParameters.isNotEmpty()) {
        parameterizedStarsBeanClassName = beanClassName
            .parameterizedBy(typeParameters.map { STAR })
        parameterizedAnyBeanClassName = beanClassName
            .parameterizedBy(typeParameters.map { ANY })
    }
    val objectName = beanName + "Serializer"
    logger.info("Generating $packageName.$objectName")
    val serializerClassName = ClassName(packageName, objectName)
    val listKTypeProjection = ClassName("kotlin.collections", "List")
        .parameterizedBy(ClassName("kotlin.reflect", "KTypeProjection"))
    val file = FileSpec.builder(packageName, objectName)
        .addImport(XmlSymbolProcessor.LIBRARY_PACKAGE, "GlobalSerializersLibrary")
        .addImport("kotlin.reflect", "KClass")
        .addImport("${XmlSymbolProcessor.LIBRARY_PACKAGE}.xml", "tag")
        .addImport("${XmlSymbolProcessor.LIBRARY_PACKAGE}.xml.XmlReader", "readXml")
        .addImport("${XmlSymbolProcessor.LIBRARY_PACKAGE}.xml.error", "InvalidXml")
        .addImport("${XmlSymbolProcessor.LIBRARY_PACKAGE}.xml.model", "TagXmlElement")
        .addImport("${XmlSymbolProcessor.LIBRARY_PACKAGE}.exception", "SerializeException", "DeserializeException")
        .declareImportsOfFields(classToGenerate)
        .declareImportsConverter(classToGenerate)
        .addType(
            TypeSpec.objectBuilder(objectName)
                .superclass(
                    ClassName(
                        "${XmlSymbolProcessor.LIBRARY_PACKAGE}.serializers",
                        "ObjectSerializer"
                    ).parameterizedBy(parameterizedStarsBeanClassName ?: beanClassName)
                )
                .addProperty(
                    PropertySpec.builder(
                        "rootName",
                        String::class,
                        KModifier.OVERRIDE
                    )
                        .initializer("\"${classToGenerate.rootName}\"")
                        .build()
                )
                .addFunction(
                    FunSpec.builder("buildXml")
                        .uncheckedCastAnnotationIfNeed(typeParameters.isNotEmpty())
                        .addModifiers(KModifier.OVERRIDE)
                        .addParameter("tagFather", TagFather::class)
                        .addParameter("obj", parameterizedStarsBeanClassName ?: beanClassName)
                        .addParameter("genericTypeList", listKTypeProjection)
                        .generateSerialization(classToGenerate)
                        .build()
                ).addFunction(
                    FunSpec.builder("readData")
                        .uncheckedCastAnnotationIfNeed(typeParameters.isNotEmpty())
                        .addModifiers(KModifier.OVERRIDE)
                        .addParameter(
                            "element",
                            XmlElement::class.asTypeName().copy(nullable = true)
                        )
                        .addParameter("genericTypeList", listKTypeProjection)
                        .returns(parameterizedAnyBeanClassName ?: beanClassName)
                        .generateDeserialization(classToGenerate, logger)
                        .build()
                ).build()
        ).build()
    createNewFile(
        Dependencies(false),
        packageName,
        objectName
    ).use { stream ->
        OutputStreamWriter(stream, StandardCharsets.UTF_8).use { writer ->
            file.writeTo(writer)
        }
    }
    return GeneratedSerializerSpec(beanClassName, serializerClassName, packageName)
}

private fun FunSpec.Builder.uncheckedCastAnnotationIfNeed(need: Boolean): FunSpec.Builder {
    return if (need) {
        addAnnotation(
            AnnotationSpec.builder(Suppress::class)
                .addMember("\"UNCHECKED_CAST\"")
                .build()
        )
    } else {
        this
    }
}

private fun FileSpec.Builder.declareImportsConverter(classToGenerate: ClassToGenerate): FileSpec.Builder {
    val converters = classToGenerate.propertyElements
        .mapNotNull { property ->
            property.converterType
        }
        .toSet()
    if (converters.isNotEmpty()) {
        addImport("${XmlSymbolProcessor.LIBRARY_PACKAGE}.serializers", "ValueSerializer")
        converters.forEach { type ->
            val declaration = type.declaration
            addImport(declaration.packageName.asString(), declaration.simpleName.asString())
        }
    }
    return this
}

private fun FileSpec.Builder.declareImportsOfFields(classToGenerate: ClassToGenerate): FileSpec.Builder {
    classToGenerate.propertyElements.map { property ->
        val type = property.propertyType.resolve()
        val packageName = type.declaration.packageName.asString()
        val simpleName = type.declaration.simpleName.asString()
        packageName to simpleName
    }
        .toSet()
        .filter { (packageName, _) ->
            packageName != classToGenerate.packagePath &&
                    packageName != "kotlin" &&
                    packageName != "kotlin.collections"
        }
        .forEach { (packageName, simpleName) ->
            addImport(packageName, simpleName)
        }
    return this
}