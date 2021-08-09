package ua.vald_zx.simplexml.ksp.processor


internal fun ClassToGenerate.toDom(): List<DomElement> {
    val firstLayerFields = propertyElements
        .filter { it.xmlPath.isEmpty() }
        .map { DomElement(it) }.toMutableList()

    propertyElements.filter {
        it.xmlPath.isNotEmpty()
    }.map { field ->
        val path = field.xmlPath.split("/")
        addLayer(firstLayerFields, path[0], path.subList(1, path.size), field)
    }
    return firstLayerFields
}

private fun addLayer(
    currentLayerFields: MutableList<DomElement>,
    currentPath: String,
    path: List<String>,
    property: PropertyElement
) {
    if (currentPath.isEmpty()) {
        val layerField = currentLayerFields.find { it.xmlName == property.xmlName }
        if (layerField != null && (layerField.propertyName.isNotEmpty() || layerField.children.any { it.type != XmlUnitType.ATTRIBUTE })) {
            error("already has data tag with name ${property.xmlName}")
        } else if (layerField?.propertyName?.isEmpty() == true) {
            val itemIndex = currentLayerFields.indexOf(layerField)
            currentLayerFields[itemIndex] =
                DomElement(
                    property = property,
                    children = layerField.children
                )
        } else {
            currentLayerFields.add(
                DomElement(property = property)
            )
        }
    } else {
        val currentPathUnit = currentLayerFields.find { it.xmlName == currentPath }
            ?: DomElement(xmlName = currentPath).apply { currentLayerFields.add(this) }
        if (property.xmlType == XmlUnitType.ATTRIBUTE && path.isEmpty()) {
            currentPathUnit.children.add(
                DomElement(property = property)
            )
        } else if (path.isEmpty()) {
            addLayer(currentPathUnit.children, "", emptyList(), property)
        } else {
            addLayer(currentPathUnit.children, path[0], path.subList(1, path.size), property)
        }
    }
}