package ua.vald_zx.simplexml.ksp.processor


internal fun makeDom(toGenerate: ClassToGenerate): List<Field> {
    val propertyElements = toGenerate.fields
    val firstLayerFields = propertyElements
        .filter { it.path.isEmpty() }
        .toMutableList()

    propertyElements.filter { it.path.isNotEmpty() }.map { field ->
        val path = field.path.split("/")
        addLayer(firstLayerFields, path[0], path.subList(1, path.size), field)
    }
    return firstLayerFields
}

private fun addLayer(
    currentLayerFields: MutableList<Field>,
    currentPath: String,
    path: List<String>,
    field: Field
) {
    if (currentPath.isEmpty() && field is Field.IsTag) {
        val layerField =
            currentLayerFields.find { it is Field.IsTag && it.tagName == field.tagName }
        if (layerField != null && layerField is Field.IsTag && (layerField.fieldName.isNotEmpty() || layerField.children.any { it !is Field.Attribute })) {
            error("already has data tag with name ${field.tagName}")
        } else if (layerField?.fieldName?.isEmpty() == true && layerField is Field.IsTag) {
            val itemIndex = currentLayerFields.indexOf(layerField)
            field.children.addAll(layerField.children)
            currentLayerFields[itemIndex] = field
        } else {
            currentLayerFields.add(field)
        }
    } else {
        val currentPathUnit =
            (currentLayerFields.find { it is Field.IsTag && it.tagName == currentPath } as Field.IsTag?)
                ?: Field.Tag(tagName = currentPath).apply { currentLayerFields.add(this) }
        if (field is Field.Attribute && path.isEmpty()) {
            currentPathUnit.children.add(field)
        } else if (path.isEmpty()) {
            addLayer(currentPathUnit.children, "", emptyList(), field)
        } else {
            addLayer(currentPathUnit.children, path[0], path.subList(1, path.size), field)
        }
    }
}