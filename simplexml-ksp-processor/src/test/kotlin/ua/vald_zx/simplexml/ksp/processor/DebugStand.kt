package ua.vald_zx.simplexml.ksp.processor

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import kotlin.test.Test
import kotlin.test.assertEquals

class DebugStand {

    @Test
    fun `Compile GenericBean test`() {
        val result = SourceFile.kotlin(
            "GenericBean.kt", """
        import ua.vald_zx.simplexml.ksp.Element
        
        class GenericBean<T1, T2> {
            @Element
            var somObject1: T1? = null
        
            @Element
            var somObject2: T2? = null
        }

        class GenericClass<T> {
            @Element
            var somObject: T? = null
        }
        
        class OneDeepGenericClass<T> {
            @Element
            var oneDeepObject1: GenericClass<T>? = null
        }

        class TwoDeepGenericsClass<T1, T2> {
            @Element
            var twoDeepFirstArg: OneDeepGenericClass<T1>? = null
        
            @Element
            var oneDeepBothArgs: GenericBean<T1, T2>? = null
        
            @Element
            var oneDeepSecondArg: GenericClass<T2>? = null
        
            @Element
            var oneDeepFirstHardSecondArg: GenericBean<T2, T1>? = null
        }
    """
        ).compile()
        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)
    }

    @Test
    fun `Same tag name error`() {
        val result = SourceFile.kotlin(
            "GenericBean.kt", """
        package test
        
        import ua.vald_zx.simplexml.ksp.Element
        
        class GenericBean<T1, T2> {
            @Element(name = "Type")
            var somObject1: T1? = null
        
            @Element(name = "Type")
            var somObject2: T2? = null
        }
    """
        ).compile()
        assertEquals(result.exitCode, KotlinCompilation.ExitCode.COMPILATION_ERROR)
    }

}