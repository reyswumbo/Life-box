package com.reyswumbo.lifeinbox

import android.app.Application
import com.reyswumbo.lifeinbox.di.AppContainer

class LifeInboxApplication : Application() {

    val container: AppContainer by lazy { AppContainer(this) }
}
