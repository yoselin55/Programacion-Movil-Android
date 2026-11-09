package com.flores.mycar

import android.app.Application

/** Application: guarda el AppContainer mientras viva la app. */
class MycarApp : Application() {
    val container: AppContainer by lazy { AppContainer() }
}
