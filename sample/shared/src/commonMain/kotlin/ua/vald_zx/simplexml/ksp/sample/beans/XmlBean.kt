package ua.vald_zx.simplexml.ksp.sample.beans

import ua.vald_zx.simplexml.ksp.Attribute
import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.Path
import ua.vald_zx.simplexml.ksp.Root

@Root("XmlBeanRootTag")
data class XmlBean(
    @Element("Layer0TagString0")
    var layer0TagString0: String = "",
    @Element
    var layer0TagString1: String = "",
    @field:[Path("Layer0Path0") Element("Layer1TagString0", false)]
    var layer1Path0TagString0: String = "",
    @field:[Path("Layer0Path0") Element(required = false)]
    var layer1Path0TagString1: String = "",
    @field:[Path("Layer0Path0/Layer1Path0") Element("Layer2Path0TagString0", false)]
    var layer2Path0TagString0: String = "",
    @field:[Path("Layer0Path0/Layer1Path0") Element("Layer2Path0TagString1", false)]
    var layer2Path0TagString1: String = "",
    @field:[Path("Layer0Path0/Layer1Path0/Layer2Path0") Element("Layer3Path0TagString0", false)]
    var layer3Path0TagString0: String = "",
    @field:[Path("Layer0Path0/Layer1Path0/Layer2Path0") Element("Layer3Path0TagString1", false)]
    var layer3Path0TagString1: String = "",
    @field:[Path("Layer0Path1/Layer1Path1/Layer2Path1") Element("Layer3Path1TagString1", false)]
    var layer3Path1TagString1: String = "",
    @field:[Path("Layer0Path1/Layer1Path1/Layer2Path1") Element("Layer3Path1TagString0", false)]
    var layer3Path1TagString0: String = "",
    @field:[Path("Layer0Path1/Layer1Path1") Element("Layer2Path1TagString1", false)]
    var layer2Path1TagString1: String = "",
    @field:[Path("Layer0Path1/Layer1Path1") Element("Layer2Path1TagString0", false)]
    var layer2Path1TagString0: String = "",
    @field:[Path("Layer0Path1") Element("Layer1Path1TagString1", false)]
    var layer1Path1TagString1: String = "",
    @field:[Path("Layer0Path1") Element("Layer1Path1TagString0", false)]
    var layer1Path1TagString0: String = "",
    @Element("Layer0TagString4")
    var layer0TagString4: String = "",
    @Element("Layer0TagString3")
    var layer0TagString3: String = "",
    @field:[Path("Layer0Path2") Attribute("Layer0Path2String0")]
    var layer0Path2AttributeString0: String = "",
    @field:[Path("Layer0Path2") Attribute("Layer0Path2String1")]
    var layer0Path2AttributeString1: String = "",
    @field:[Path("Layer0Path3") Attribute("Layer0Path3String0")]
    var layer0Path3AttributeString0: String = "",
    @field:[Path("Layer0Path3") Attribute("Layer0Path3String1")]
    var layer0Path3AttributeString1: String = "",
    @field:[Path("Layer0Path4/Layer1Path4") Attribute("Layer1Path4String0")]
    var layer1Path4AttributeString0: String = "",
    @field:[Path("Layer0Path4/Layer1Path4") Attribute("Layer1Path4String1")]
    var layer1Path4AttributeString1: String = "",
    @field:[Path("Layer0Path5/Layer1Path5") Attribute("Layer1Path5String0")]
    var layer1Path5AttributeString0: String = "",
    @field:[Path("Layer0Path5/Layer1Path5") Attribute("Layer1Path5String1")]
    var layer1Path5AttributeString1: String = "",
    @Element("Layer0Path6Tag")
    var Layer0Path6Tag: String = "",
    @field:[Path("Layer0Path6Tag") Attribute("Layer0Path6String0")]
    var layer0Path6AttributeString0: String = "",
    @field:[Path("Layer0Path6Tag") Attribute]
    var layer0Path6AttributeString1: String = "",
)