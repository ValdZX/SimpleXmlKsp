package ua.vald_zx.simplexml.ksp.test.list

import ua.vald_zx.simplexml.ksp.Convert
import ua.vald_zx.simplexml.ksp.Converter
import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.ElementList

data class ConstructorFieldListOfStrings(
    @ElementList(name = "List", entry = "Entry")
    val list: List<String>
)

data class ConstructorInlineFieldListOfStrings(
    @ElementList(entry = "Entry", inline = true)
    val list: List<String>
)

data class ConstructorFieldListOfStringsWithoutName(
    @ElementList(entry = "Entry")
    val list: List<String>
)

data class ConstructorFieldListOfStringsWithoutEntry(
    @ElementList(name = "List")
    val list: List<String>
)

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

data class ConstructorFieldListOfObjects(
    @ElementList(name = "List", entry = "Entry")
    val list: List<ListItem>
)

data class ConstructorInlineFieldListOfObjects(
    @ElementList(entry = "Entry", inline = true)
    val list: List<ListItem>
)

data class ConstructorFieldListOfObjectsWithConvert(
    @field:[ElementList(name = "List", entry = "Entry") Convert(ListItemConverter::class)]
    val list: List<ListItem>
)

data class FieldListOfStrings(
    @ElementList(name = "List", entry = "Entry")
    val list: List<String>
)

data class FieldInlineListOfStrings(
    @ElementList(entry = "Entry", inline = true)
    val list: List<String>
)
