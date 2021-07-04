package ua.vald_zx.simplexml.ksp.test.required

import ua.vald_zx.simplexml.ksp.Element

data class RequiredConstructorField(
    @Element(required = true)
    var tag: String
)

data class RequiredConstructorNullableField(
    @Element(required = true)
    var tag: String?
)

data class RequiredConstructorFieldWithDefault(
    @Element(required = true)
    var tag: String = "Default"
)

data class NotRequiredConstructorFieldWithDefault(
    @Element(required = false)
    var tag: String = "Default"
)

data class RequiredConstructorNullableFieldWithDefault(
    @Element(required = true)
    var tag: String? = "Default"
)

class RequiredField {
    @Element(required = true)
    var tag: String = ""
}

class NotRequiredField {
    @Element(required = false)
    var tag: String = ""
}

class RequiredNullableField {
    @Element(required = true)
    var tag: String? = null
}

class NotRequiredNullableField {
    @Element(required = false)
    var tag: String? = null
}

class RequiredFieldWithDefault {
    @Element(required = true)
    var tag: String = "Default"
}

class NotRequiredFieldWithDefault {
    @Element(required = false)
    var tag: String = "Default"
}

class RequiredNullableFieldWithDefault {
    @Element(required = true)
    var tag: String? = "Default"
}

class NotRequiredNullableFieldWithDefault {
    @Element(required = false)
    var tag: String? = "Default"
}