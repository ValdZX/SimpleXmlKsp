package ua.vald_zx.simplexml.ksp.test.text

import ua.vald_zx.simplexml.ksp.Attribute
import ua.vald_zx.simplexml.ksp.Root
import ua.vald_zx.simplexml.ksp.Text

@Root("Tag")
data class ConstructorText(
    @Text
    val text: String
)

@Root("Tag")
data class ConstructorTextWithAttribute(
    @Text
    val text: String,
    @Attribute
    val attr: String
)

@Root("Tag")
data class ConstructorNullableText(
    @Text
    var text: String?
)

@Root("Tag")
data class ConstructorNullableTextNotRequired(
    @Text(required = false)
    var text: String?
)

@Root("Tag")
class FieldText {
    @Text
    var text: String = ""
}

@Root("Tag")
class FieldNullableText {
    @Text
    var text: String? = null
}

@Root("Tag")
class FieldNullableNotRequiredText {
    @Text(required = false)
    var text: String? = null
}