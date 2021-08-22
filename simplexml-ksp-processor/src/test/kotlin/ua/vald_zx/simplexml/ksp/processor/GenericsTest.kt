package ua.vald_zx.simplexml.ksp.processor

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import kotlin.test.Test
import kotlin.test.assertEquals

class GenericsTest {

    @Test
    fun `Compile GenericBean test`() {
        val result = SourceFile.kotlin(
            "EmptyClass.kt", """
        import ua.vald_zx.simplexml.ksp.Element

        open class GenericBean<T1, T2> {
            @Element
            var somObject1: T1? = null
        
            @Element
            var somObject2: T2? = null
        }
    """
        ).compile()

        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)
    }

}