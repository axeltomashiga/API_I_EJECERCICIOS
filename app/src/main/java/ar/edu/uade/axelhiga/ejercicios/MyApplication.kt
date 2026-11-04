package ar.edu.uade.axelhiga.ejercicios

import android.app.Application
import ar.edu.uade.axelhiga.ejercicios.ej11.AppContainer

class MyApplication : Application() {

    // Contenedor manual de dependencias que existirá mientras viva la app.
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
