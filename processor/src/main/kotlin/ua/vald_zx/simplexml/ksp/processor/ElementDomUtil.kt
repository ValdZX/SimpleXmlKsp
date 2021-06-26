package ua.vald_zx.simplexml.ksp.processor


internal fun ClassToGenerate.toDom(): List<FieldElement> {
    val firstLayerFields = properties
        .filter { it.path.isEmpty() }
        .map { FieldElement(it.xmlName, it.name) }.toMutableList()
    properties.filter {
        it.path.isNotEmpty()
    }.map { field ->
        val path = field.path.split("/")
        addLayer(firstLayerFields, path[0], path.subList(1, path.size), field)
    }
    return firstLayerFields
}

private fun addLayer(
    currentLayerFields: MutableList<FieldElement>,
    currentPath: String,
    path: List<String>,
    field: Property
) {
    if (currentPath.isEmpty()) {
        val layerField = currentLayerFields.find { it.name == field.xmlName }
        if (layerField != null && (layerField.propertyName.isNotEmpty() || layerField.children.any { it.type != XmlUnitType.ATTRIBUTE })) {
            error("already has data tag with name ${field.xmlName}")
        } else if (layerField?.propertyName?.isEmpty() == true) {
            val itemIndex = currentLayerFields.indexOf(layerField)
            currentLayerFields[itemIndex] = layerField.copy(propertyName = field.name)
        } else {
            currentLayerFields.add(
                FieldElement(
                    field.xmlName,
                    field.name,
                    type = field.xmlType
                )
            )
        }
    } else {
        val currentPathUnit = currentLayerFields.find { it.name == currentPath }
            ?: FieldElement(currentPath)
                .apply { currentLayerFields.add(this) }
        if (field.xmlType == XmlUnitType.ATTRIBUTE && path.isEmpty()) {
            currentPathUnit.children.add(
                FieldElement(
                    field.xmlName,
                    field.name,
                    type = field.xmlType
                )
            )
        } else if (path.isEmpty()) {
            addLayer(currentPathUnit.children, "", emptyList(), field)
        } else {
            addLayer(currentPathUnit.children, path[0], path.subList(1, path.size), field)
        }
    }
}