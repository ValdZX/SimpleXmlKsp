package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration

class ClassToGenerateExtension(
    val xmlBean: KSClassDeclaration,
    val elements: MutableList<XmlElement> = mutableListOf()
)

class XmlElement(
    val name: String,
    val required: Boolean,
    val path: String = ""
)