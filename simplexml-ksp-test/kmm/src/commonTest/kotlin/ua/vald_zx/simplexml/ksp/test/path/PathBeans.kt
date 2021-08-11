package ua.vald_zx.simplexml.ksp.test.path

import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.Path

/*
<ElementWithOneLayerPath>
    <Layer1>
        <tag>ElementWithOneLayerPath</tag>
    </Layer1>
</ElementWithOneLayerPath>
 */
data class ElementWithOneLayerPath(
    @field:[Path("Layer1") Element]
    var tag: String
)

/*
<ElementWithTwoLayerPath>
    <Layer1>
        <Layer2>
            <tag>ElementWithTwoLayerPath</tag>
        </Layer2>
    </Layer1>
</ElementWithTwoLayerPath>
 */
data class ElementWithTwoLayerPath(
    @field:[Path("Layer1/Layer2") Element]
    var tag: String
)

/*
<ElementWithThreeLayerPath>
    <Layer1>
        <Layer2>
            <Layer3>
                <tag>ElementWithThreeLayerPath</tag>
            </Layer3>
        </Layer2>
    </Layer1>
</ElementWithThreeLayerPath>
 */
data class ElementWithThreeLayerPath(
    @field:[Path("Layer1/Layer2/Layer3") Element]
    var tag: String
)

/*
<TwoElementsOnFirstLayerPath>
    <Layer1>
        <tag1>Data1</tag1>
        <tag2>Data2</tag2>
    </Layer1>
</TwoElementsOnFirstLayerPath>
 */
data class TwoElementsOnFirstLayerPath(
    @field:[Path("Layer1") Element]
    var tag1: String,
    @field:[Path("Layer1") Element]
    var tag2: String
)

/*
<TwoElementsOnDiffLayersPath>
    <Layer1>
        <tag1>Data1</tag1>
        <Layer2>
            <tag2>Data2</tag2>
        </Layer2>
    </Layer1>
</TwoElementsOnDiffLayersPath>
 */

data class TwoElementsOnDiffLayersPath(
    @field:[Path("Layer1") Element]
    var tag1: String,
    @field:[Path("Layer1/Layer2") Element]
    var tag2: String
)