package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration

val KSClassDeclaration.fullName: String
    get() = packageName.asString() + "." + simpleName.asString()
