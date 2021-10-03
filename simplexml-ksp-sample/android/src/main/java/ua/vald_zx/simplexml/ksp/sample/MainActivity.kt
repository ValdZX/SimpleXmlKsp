package ua.vald_zx.simplexml.ksp.sample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ua.vald_zx.simplexml.ksp.SimpleXml

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val xmlTextView = findViewById<TextView>(R.id.xmlTextView)
        val bean = PackageDto(
            serviceName = "GET_INFO",
            token = "S290bGluIGlzIGF3ZXNvbWU=",
            location = "Ukraine, Kharkiv",
            latitude = 50.004977,
            longitude = 36.231117
        )
        SimpleXml.pretty = true
        val xml: String = SimpleXml.serialize(bean)
        xmlTextView.text = xml
    }
}