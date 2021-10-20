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

data class GenericData<T>(
    @Element
    var somObject1: T,
)

data class OneDeepGenericData<T>(
    @Element
    var oneDeepObject1: GenericData<T>,
)

data class TwoDeepGenericData<T>(
    @Element
    var twoDeepObject1: OneDeepGenericData<T>,
)

class GenericClass<T> {
    @Element
    var somObject: T? = null
}

class OneDeepGenericClass<T> {
    @Element
    var oneDeepObject1: GenericClass<T>? = null
}

class TwoDeepGenericClass<T> {
    @Element
    var twoDeepObject1: OneDeepGenericClass<T>? = null
}

class TwoDeepGenericsClass<T1, T2> {
    @Element
    var twoDeepFirstArg: OneDeepGenericClass<T1>? = null

    @Element
    var oneDeepBothArgs: GenericBean<T1, T2>? = null

    @Element
    var oneDeepSecondArg: GenericClass<T2>? = null

    @Element
    var oneDeepFirstHardSecondArg: GenericBean<GenericClass<T2>, T1>? = null
}