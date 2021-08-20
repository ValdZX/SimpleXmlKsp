package ua.vald_zx.simplexml.ksp.processor

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.symbolProcessorProviders

fun SourceFile.compile(): KotlinCompilation.Result {
    val compilation = KotlinCompilation().apply {
        sources = listOf(this@compile)
        symbolProcessorProviders = listOf(XmlSymbolProcessorProvider())
        inheritClassPath = true
        verbose = false
        kspIncremental = true
    }
    return compilation.compile()
}