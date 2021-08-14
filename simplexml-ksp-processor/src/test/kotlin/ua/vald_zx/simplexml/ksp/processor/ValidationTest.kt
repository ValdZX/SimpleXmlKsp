package ua.vald_zx.simplexml.ksp.processor

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import kotlin.test.Test
import kotlin.test.assertEquals

class ValidationTest {

    @Test
    fun `Compile empty class test`() {
        val source = SourceFile.kotlin(
            "EmptyClass.kt", """
        class EmptyClass()
    """
        )
        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            symbolProcessorProviders = listOf(XmlSymbolProcessorProvider())
        }
        val result = compilation.compile()
        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)
    }
}