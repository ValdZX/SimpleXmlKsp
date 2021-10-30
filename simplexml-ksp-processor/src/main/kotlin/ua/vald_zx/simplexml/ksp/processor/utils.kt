package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration

val KSClassDeclaration.fullName: String
    get() = packageName.asString() + "." + simpleName.asString()

val KSClassDeclaration.logDirection: String
    get() = "Class ${simpleName.asString()}(${containingFile?.fileName}:0)"

fun Field.logDirection(parent: KSClassDeclaration): String {
    return parent.logDirection + "::" + fieldName
}

fun String.intersect(other: String): String {
    val thisString = this
    val inter = (thisString.toByteArray() intersect other.toByteArray().toList()).toByteArray()
    return String(inter)
}

