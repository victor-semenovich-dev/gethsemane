package by.geth.gethsemane

import android.app.Application
import by.geth.gethsemane.di.initKoin
import org.koin.android.ext.koin.androidContext

class GethApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@GethApplication)
        }
    }
}