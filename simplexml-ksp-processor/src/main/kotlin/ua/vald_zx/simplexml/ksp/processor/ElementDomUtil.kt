package ua.vald_zx.simplexml.ksp.processor


internal fun makeDom(toGenerate: ClassToGenerate): List<Field> {
    val propertyElements = toGenerate.fields
    val firstLayerFields = propertyElements
        .filter { it.path.isEmpty() }
        .toMutableList()

    propertyElements.filter {
        it.path.isNotEmpty()
    }.map { field ->
        val path = field.path.split("/")
        addLayer(firstLayerFields, path[0], path.subList(1, path.size), field)
    }
    return firstLayerFields
}

private fun addLayer(
    currentLayerFields: MutableList<Field>,
    currentPath: String,
    path: List<String>,
    property: Field
) {
    if (currentPath.isEmpty() && property is Field.IsTag) {
        val layerField =
            currentLayerFields.find { it is Field.IsTag && it.tagName == property.tagName }
        if (layerField != null && layerField is Field.IsTag && (layerField.fieldName.isNotEmpty() || layerField.children.any { it !is Field.Attribute })) {
            error("already has data tag with name ${property.tagName}")
        } else if (layerField?.fieldName?.isEmpty() == true && layerField is Field.IsTag) {
            val itemIndex = currentLayerFields.indexOf(layerField)
            property.children.addAll(layerField.children)
            currentLayerFields[itemIndex] = property
        } else {
            currentLayerFields.add(property)
        }
    } else {
        val currentPathUnit =
            (currentLayerFields.find { it is Field.IsTag && it.tagName == currentPath } as Field.IsTag?)
                ?: Field.Tag(tagName = currentPath).apply { currentLayerFields.add(this) }
        if (property is Field.Attribute && path.isEmpty()) {
            currentPathUnit.children.add(property)
        } else if (path.isEmpty()) {
            addLayer(currentPathUnit.children, "", emptyList(), property)
        } else {
            addLayer(currentPathUnit.children, path[0], path.subList(1, path.size), property)
        }
    }
}