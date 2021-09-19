package custom

import ua.vald_zx.simplexml.ksp.Attribute
import ua.vald_zx.simplexml.ksp.ElementMap
import ua.vald_zx.simplexml.ksp.Path

/*
<AndroidStringResourcesChild>
    <resources xmlns:android="http://schemas.android.com/apk/res/android">
        <string name="appName">The best app</string>
        <string name="greetings">Hello!</string>
    </resources>
</AndroidStringResourcesChild>
 */
data class AndroidStringResourcesChild(
    @ElementMap(name = "resources", key = "name", entry = "string", attribute = true)
    val resources: Map<String, String>,

    @field:[Path("resources") Attribute("xmlns:android")]
    var ns: String = "http://schemas.android.com/apk/res/android",
)