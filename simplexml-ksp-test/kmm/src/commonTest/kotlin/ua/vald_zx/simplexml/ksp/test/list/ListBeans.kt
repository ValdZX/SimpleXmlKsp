package ua.vald_zx.simplexml.ksp.test.list

import ua.vald_zx.simplexml.ksp.*

/*
<ConstructorFieldListOfStrings>
    <List>
        <Entry>Value1</Entry>
        <Entry>Value2</Entry>
        <Entry>Value3</Entry>
    </List>
</ConstructorFieldListOfStrings>
 */
data class ConstructorFieldListOfStrings(
    @ElementList(name = "List", entry = "Entry")
    val list: List<String>
)

/*
<ConstructorFieldListOfStringsNullable>
    <List>
        <Entry>Value1</Entry>
        <Entry>Value2</Entry>
        <Entry>Value3</Entry>
    </List>
</ConstructorFieldListOfStringsNullable>
 */
data class ConstructorFieldListOfStringsNullable(
    @ElementList(name = "List", entry = "Entry")
    var list: List<String>?
)

/*
<ConstructorInlineFieldListOfStrings>
    <Entry>Value1</Entry>
    <Entry>Value2</Entry>
    <Entry>Value3</Entry>
</ConstructorInlineFieldListOfStrings>
 */
data class ConstructorInlineFieldListOfStrings(
    @ElementList(entry = "Entry", inline = true)
    val list: List<String>
)

/*
<ConstructorFieldListOfStringsWithoutName>
    <list>
        <Entry>Value1</Entry>
        <Entry>Value2</Entry>
        <Entry>Value3</Entry>
    </list>
</ConstructorFieldListOfStringsWithoutName>
 */
data class ConstructorFieldListOfStringsWithoutName(
    @ElementList(entry = "Entry")
    val list: List<String>
)

/*
<ConstructorFieldListOfStringsWithoutEntry>
    <List>
        <String>Value1</String>
        <String>Value2</String>
        <String>Value3</String>
    </List>
</ConstructorFieldListOfStringsWithoutEntry>
 */
data class ConstructorFieldListOfStringsWithoutEntry(
    @ElementList(name = "List")
    val list: List<String>
)

/*
<ConstructorInlineFieldListOfStringsWithoutEntry>
    <String>Value1</String>
    <String>Value2</String>
    <String>Value3</String>
</ConstructorInlineFieldListOfStringsWithoutEntry>
 */
data class ConstructorInlineFieldListOfStringsWithoutEntry(
    @ElementList(inline = true)
    val list: List<String>
)

data class ListItem(
    @Element
    val tag1: String,
    @Element
    val tag2: Short
)

class ListItemConverter : Converter<ListItem> {
    override fun write(obj: ListItem): String {
        return obj.tag1 + "|||" + obj.tag2
    }

    override fun read(raw: String): ListItem {
        val split = raw.split("|||")
        return ListItem(split[0], split[1].toShort())
    }
}

/*
<ConstructorFieldListOfObjects>
    <List>
        <Entry>
            <tag1>Value1</tag1>
            <tag2>1</tag2>
        </Entry>
        <Entry>
            <tag1>Value2</tag1>
            <tag2>2</tag2>
        </Entry>
        <Entry>
            <tag1>Value3</tag1>
            <tag2>3</tag2>
        </Entry>
    </List>
</ConstructorFieldListOfObjects>
 */
data class ConstructorFieldListOfObjects(
    @ElementList(name = "List", entry = "Entry")
    val list: List<ListItem>
)

/*
<ConstructorInlineFieldListOfObjects>
    <Entry>
        <tag1>Value1</tag1>
        <tag2>1</tag2>
    </Entry>
    <Entry>
        <tag1>Value2</tag1>
        <tag2>2</tag2>
    </Entry>
    <Entry>
        <tag1>Value3</tag1>
        <tag2>3</tag2>
    </Entry>
</ConstructorInlineFieldListOfObjects>
 */
data class ConstructorInlineFieldListOfObjects(
    @ElementList(entry = "Entry", inline = true)
    val list: List<ListItem>
)

data class ConstructorFieldListOfObjectsWithConvert(
    @field:[ElementList(name = "List", entry = "Entry") Convert(ListItemConverter::class)]
    val list: List<ListItem>
)

/*
<FieldListOfStrings>
    <List>
        <Entry>Value1</Entry>
        <Entry>Value2</Entry>
        <Entry>Value3</Entry>
    </List>
</FieldListOfStrings>
 */
data class FieldListOfStrings(
    @ElementList(name = "List", entry = "Entry")
    val list: List<String>
)

/*
<FieldInlineListOfStrings>
    <Entry>Value1</Entry>
    <Entry>Value2</Entry>
    <Entry>Value3</Entry>
</FieldInlineListOfStrings>
 */
data class FieldInlineListOfStrings(
    @ElementList(entry = "Entry", inline = true)
    val list: List<String>
)

/*
<ConstructorFieldMutableListOfStrings>
    <List>
        <Entry>Value1</Entry>
        <Entry>Value2</Entry>
        <Entry>Value3</Entry>
    </List>
</ConstructorFieldMutableListOfStrings>
 */
data class ConstructorFieldMutableListOfStrings(
    @ElementList(name = "List", entry = "Entry")
    val list: MutableList<String>
)

/*
<ConstructorFieldMutableListOfStrings>
    <List size="3">
        <Entry>Value1</Entry>
        <Entry>Value2</Entry>
        <Entry>Value3</Entry>
    </List>
</ConstructorFieldMutableListOfStrings>
 */
data class ConstructorFieldMutableListOfStringsWithAttribute(
    @ElementList(name = "List", entry = "Entry")
    val list: MutableList<String>,
    @Path("List")
    @Attribute
    val size: Int
)

/*
<ConstructorFieldListOfStringsNullableWithAttribute>
    <List size="3">
        <Entry>Value1</Entry>
        <Entry>Value2</Entry>
        <Entry>Value3</Entry>
    </List>
</ConstructorFieldListOfStringsNullableWithAttribute>
 */
data class ConstructorFieldListOfStringsNullableWithAttribute(
    @ElementList(name = "List", entry = "Entry")
    var list: List<String>?,
    @Path("List")
    @Attribute
    val size: Int
)

/*
<FieldMutableListOfStrings>
    <List>
        <Entry>Value1</Entry>
        <Entry>Value2</Entry>
        <Entry>Value3</Entry>
    </List>
</FieldMutableListOfStrings>
 */
class FieldMutableListOfStrings {
    @ElementList(name = "List", entry = "Entry")
    var list: MutableList<String> = mutableListOf()
}

/*
<NullableFieldMutableListOfStrings>
    <List>
        <Entry>Value1</Entry>
        <Entry>Value2</Entry>
        <Entry>Value3</Entry>
    </List>
</NullableFieldMutableListOfStrings>
 */
class NullableFieldMutableListOfStrings {
    @ElementList(name = "List", entry = "Entry")
    var list: MutableList<String>? = null
}