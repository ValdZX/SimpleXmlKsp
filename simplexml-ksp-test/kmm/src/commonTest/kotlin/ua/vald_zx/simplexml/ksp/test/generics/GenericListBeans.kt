package ua.vald_zx.simplexml.ksp.test.generics

import ua.vald_zx.simplexml.ksp.*

/*
<GenericConstructorArgListOfStrings>
    <List>
        <Entry>Value1</Entry>
        <Entry>Value2</Entry>
        <Entry>Value3</Entry>
    </List>
</GenericConstructorArgListOfStrings>
 */
data class GenericConstructorArgListOfStrings<T>(
    @ElementList(name = "List", entry = "Generic")
    var list: List<T>
)

/*
<GenericNullableConstructorArgListOfStrings>
    <List>
        <Entry>Value1</Entry>
        <Entry>Value2</Entry>
        <Entry>Value3</Entry>
    </List>
</GenericNullableConstructorArgListOfStrings>
 */
data class GenericNullableConstructorArgListOfStrings<T>(
    @ElementList(name = "List", entry = "Generic")
    var list: List<T>? = null
)

/*
<GenericNullableFieldListOfStrings>
    <List>
        <Entry>Value1</Entry>
        <Entry>Value2</Entry>
        <Entry>Value3</Entry>
    </List>
</GenericNullableFieldListOfStrings>
 */
class GenericNullableFieldListOfStrings<T> {
    @ElementList(name = "List", entry = "Generic")
    var list: List<T>? = null
}

/*
<GenericNullableFieldMutableListOfStrings>
    <List>
        <Entry>Value1</Entry>
        <Entry>Value2</Entry>
        <Entry>Value3</Entry>
    </List>
</GenericNullableFieldMutableListOfStrings>
 */
class GenericNullableFieldMutableListOfStrings<T> {
    @ElementList(name = "List", entry = "Generic")
    var list: MutableList<T>? = null
}