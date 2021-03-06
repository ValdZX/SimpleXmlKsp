package ua.vald_zx.simplexml.ksp.processor

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import kotlin.test.Test
import kotlin.test.assertEquals

class ValidationTest {

    @Test
    fun `Compile empty class test`() {
        val result = kotlin(
            "EmptyClass.kt", """
        class EmptyClass
    """
        ).compile()

        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)
    }

    @Test
    fun `Compile Element class test`() {
        val result = kotlin(
            "RequiredConstructorField.kt", """
        package test

        import ua.vald_zx.simplexml.ksp.Element

        data class RequiredConstructorField(
            @Element(required = true)
            var tag: String
        )
    """
        ).compile()
        result.generatedFiles.forEach {
            println(it.name)
        }
        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)
    }

    @Test
    fun `Compile NotRequiredConstructorField class test`() {
        val result = kotlin(
            "NotRequiredConstructorField.kt", """
        package test

        import ua.vald_zx.simplexml.ksp.Element

        data class NotRequiredConstructorField(
            @Element(required = false)
            var tag: String
        )
    """
        ).compile()
        assertEquals(result.exitCode, KotlinCompilation.ExitCode.COMPILATION_ERROR)
    }

    @Test
    fun `Compile SubTagPathAttribute class test`() {
        val result = kotlin(
            "SubTagPathAttribute.kt",
            """
        package test

        import ua.vald_zx.simplexml.ksp.Attribute
        import ua.vald_zx.simplexml.ksp.Element
        import ua.vald_zx.simplexml.ksp.Path

        data class SubTagPathAttribute(
            @field:[Path("Tag/Layer1") Attribute]
            var attr: String,
            @Element(name = "Tag")
            var tag: String
        )
    """,
        ).compile()
        assertEquals(result.exitCode, KotlinCompilation.ExitCode.COMPILATION_ERROR)
    }

    @Test
    fun `Compile Value+Variable class test`() {
        val result = kotlin(
            "SubTagPathAttribute.kt",
            """
        package test

        import ua.vald_zx.simplexml.ksp.Element

        data class RequiredConstructorField(
            @Element(required = true)
            val tag: String,
            @Element(required = true)
            var tag2: String
        )
    """,
        ).compile()
        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)
    }

    @Test
    fun `Compile MutableList class test`() {
        val result = kotlin(
            "RequiredConstructorMutableList.kt",
            """
        package test

        import ua.vald_zx.simplexml.ksp.ElementList

        data class RequiredConstructorMutableList(
            @ElementList(required = true)
            val list: MutableList<String>,
        )
    """,
        ).compile()
        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)
    }

    @Test
    fun `Compile Map class test`() {
        val result = kotlin(
            "ConstructorFieldMapOfStrings.kt",
            """
        package test

        import ua.vald_zx.simplexml.ksp.ElementMap

        data class ConstructorFieldMapOfStrings(
            @ElementMap(name = "Map", key = "Key", value = "Value")
            val map: Map<String, Int>
        )
    """,
        ).compile()
        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)
    }

    @Test
    fun `NullableTagAttribute class test`() {
        val result = kotlin(
            "ConstructorFieldMapOfStrings.kt",
            """
        package test

        import ua.vald_zx.simplexml.ksp.Attribute
        import ua.vald_zx.simplexml.ksp.Path
        import ua.vald_zx.simplexml.ksp.Element

        data class NullableTagAttribute(
            @field:[Path("Tag") Attribute]
            var attr: String,
            @Element(name = "Tag", required = false)
            var tag: String? = null
        )
    """,
        ).compile()
        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)
    }
}