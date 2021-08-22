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

open class GenericBeanWithOneParameter<T> {
    @Element
    var somObject1: T? = null

    @Element
    var somObject2: T? = null
}

open class GenericThreeBean<T1, T2, T3> {
    @Element
    var somObject1: T1? = null

    @Element
    var somObject2: T2? = null

    @Element
    var somObject3: T2? = null

    @Element
    var somObject4: T3? = null

    @Element
    var somObject5: T3? = null

    @Element
    var somObject6: T3? = null
}

class GenericExtension : GenericBean<String, Float>()