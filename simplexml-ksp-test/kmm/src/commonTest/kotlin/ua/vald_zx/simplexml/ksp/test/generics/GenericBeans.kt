package ua.vald_zx.simplexml.ksp.test.generics

import ua.vald_zx.simplexml.ksp.Element

data class GenericDataClass<T1, T2>(
    @Element
    var somObject1: T1,
    @Element
    var somObject2: T2,
)

open class GenericBean<T1, T2> {
    @Element
    var somObject1: T1? = null

    @Element
    var somObject2: T2? = null
}

class GenericExtension : GenericBean<String, Float>()