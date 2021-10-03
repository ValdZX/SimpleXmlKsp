package ua.vald_zx.simplexml.ksp.sample

import android.app.Application
import ua.vald_zx.simplexml.ksp.sample.serializaton.AppSerializersEnrolment

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppSerializersEnrolment.enrol()
    }
}