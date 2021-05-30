package ua.vald_zx.xml.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration

val KSClassDeclaration.fullName: String
    get() = packageName.asString() + "." + simpleName.asString()
